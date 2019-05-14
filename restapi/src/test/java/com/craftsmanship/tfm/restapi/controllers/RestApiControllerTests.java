package com.craftsmanship.tfm.restapi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.OperationType;
import com.craftsmanship.tfm.persistence.ItemPersistence;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
public class RestApiControllerTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiControllerTests.class);

    private static String SENDER_TOPIC = "mytopic";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, SENDER_TOPIC);

    @BeforeClass
    public static void setup() {
        System.setProperty("kafka.bootstrap-servers", embeddedKafka.getEmbeddedKafka().getBrokersAsString());
    }

    private KafkaMessageListenerContainer<String, ItemOperation> container;

    private BlockingQueue<ConsumerRecord<String, ItemOperation>> records;

    @LocalServerPort
    private int restPort;

    @Autowired
    private RestApiController restApiController;

    @Autowired
    private ItemPersistence itemPersistence;

    @Before
    public void setUp() {
        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false",
                embeddedKafka.getEmbeddedKafka());

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // create a Kafka consumer factory
        DefaultKafkaConsumerFactory<String, ItemOperation> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProperties, new StringDeserializer(), new JsonDeserializer<>(ItemOperation.class));

        // set the topic that needs to be consumed
        ContainerProperties containerProperties = new ContainerProperties(SENDER_TOPIC);

        // create a Kafka MessageListenerContainer
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        // create a thread safe queue to store the received message
        records = new LinkedBlockingQueue<>();

        // setup a Kafka message listener
        container.setupMessageListener(new MessageListener<String, ItemOperation>() {
            @Override
            public void onMessage(ConsumerRecord<String, ItemOperation> record) {
                LOGGER.debug("test-listener received message='{}'", record.toString());
                records.add(record);
            }
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        // stop the container
        container.stop();

        // empty persistence
        for (Item item : itemPersistence.list()) {
            itemPersistence.delete(item.getId());
        }
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(restApiController, is(notNullValue()));
    }

    @Test
    public void test_when_item_is_created_then_item_persisted_and_kafka_message_sent() throws InterruptedException {
        String itemDescription = "Zapato";
        Item item = new Item.Builder().withDescription(itemDescription).build();

        String url = "http://localhost:" + restPort + "/api/v1/items";
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Item> request = new HttpEntity<>(item);
        Item responseItem = restTemplate.postForObject(url, request, Item.class);

        // first, for comparison, we need to set the item id, created in persistence
        item.setId(responseItem.getId());

        // check returned item in the one created
        assertThat(responseItem, equalTo(item));

        // check item was created in persistence
        Item storedItem = itemPersistence.get(responseItem.getId());
        assertThat(storedItem, equalTo(item));

        // check that the Kafka message was received
        ConsumerRecord<String, ItemOperation> received = records.poll(10, TimeUnit.SECONDS);
        ItemOperation expectedOperation = new ItemOperation(OperationType.CREATED, item);
        assertThat(received, hasValue(expectedOperation));
    }

    @Test
    public void test_given_some_items_when_get_items_mapping_then_items_are_returned() {
        // Given
        Item item1 = new Item.Builder().withDescription("item1").build();
        Item item2 = new Item.Builder().withDescription("item2").build();
        Item item3 = new Item.Builder().withDescription("item3").build();
        itemPersistence.create(item1);
        itemPersistence.create(item2);
        itemPersistence.create(item3);

        // When
        String url = "http://localhost:" + restPort + "/api/v1/items";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Item>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Item>>() {
                });
        List<Item> items = response.getBody();

        // Then
        assertThat(items, equalTo(itemPersistence.list()));
    }

    @Test
    public void test_given_some_items_when_get_item_mapping_then_item_is_returned() {
        // Given
        Item item1 = new Item.Builder().withDescription("item1").build();
        Item item2 = new Item.Builder().withDescription("item2").build();
        Item item3 = new Item.Builder().withDescription("item3").build();
        itemPersistence.create(item1);
        itemPersistence.create(item2);
        itemPersistence.create(item3);

        // When
        Long id = 2L;
        String url = "http://localhost:" + restPort + "/api/v1/items/" + id;
        RestTemplate restTemplate = new RestTemplate();
        Item responseItem = restTemplate.getForObject(url, Item.class);

        // Then
        assertThat(responseItem, equalTo(itemPersistence.get(id)));
    }

    @Test
    public void test_given_item_when_edit_mapping_then_edited_item_is_returned() {
        // Given
        Item item1 = new Item.Builder().withDescription("item1").build();
        Item item2 = new Item.Builder().withDescription("item2").build();
        Item item3 = new Item.Builder().withDescription("item3").build();
        itemPersistence.create(item1);
        Item itemToUpdate = itemPersistence.create(item2);
        itemPersistence.create(item3);

        // When
        Long id = itemToUpdate.getId();
        Item updatedItem = new Item.Builder().withDescription("updated_item2").build();
        String url = "http://localhost:" + restPort + "/api/v1/items/" + id;
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Item> request = new HttpEntity<>(updatedItem);
        ResponseEntity<Item> response = restTemplate.exchange(url, HttpMethod.PUT, request, Item.class);
        Item responseItem = response.getBody();

        // Then
        updatedItem.setId(id);
        assertThat(responseItem, equalTo(updatedItem));
        assertThat(responseItem, equalTo(itemPersistence.get(id)));
    }

    @Test
    public void test_given_some_items_when_delete_item_mapping_then_item_is_deleted_and_kafka_message_sent()
            throws InterruptedException {
        // Given
        Item item1 = new Item.Builder().withDescription("item1").build();
        Item item2 = new Item.Builder().withDescription("item2").build();
        Item item3 = new Item.Builder().withDescription("item3").build();
        itemPersistence.create(item1);
        itemPersistence.create(item2);
        itemPersistence.create(item3);

        // When
        Long id = 2L;
        String url = "http://localhost:" + restPort + "/api/v1/items/" + id;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(url);

        // Then
        assertThat(2, equalTo(itemPersistence.list().size()));
        Item responseItem = restTemplate.getForObject(url, Item.class);
        assertThat(responseItem, is(nullValue()));

        // check that the Kafka message was received
        ConsumerRecord<String, ItemOperation> received = records.poll(10, TimeUnit.SECONDS);
        ItemOperation expectedOperation = new ItemOperation(OperationType.DELETED, item2);
        assertThat(received, hasValue(expectedOperation));
    }
}
