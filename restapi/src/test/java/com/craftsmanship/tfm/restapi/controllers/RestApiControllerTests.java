package com.craftsmanship.tfm.restapi.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.restapi.kafka.model.Item;
import com.craftsmanship.tfm.restapi.kafka.model.ItemOperation;

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
import org.springframework.http.HttpEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
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
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(restApiController).isNotNull();
    }

    @Test
    public void test_when_item_is_created_then_kafka_topic_is_added() throws InterruptedException {
        String url = "http://localhost:" + restPort + "/api/v1/items";
        RestTemplate restTemplate = new RestTemplate();
        Item item = new Item.Builder().withId(1).withDescription("Zapato").build();
        HttpEntity<Item> request = new HttpEntity<>(item);
        restTemplate.postForLocation(url, request);
        
        // check that the message was received
        ConsumerRecord<String, ItemOperation> received = records.poll(10, TimeUnit.SECONDS);

        ItemOperation expectedOperation = new ItemOperation(item);
        assertThat(received, hasValue(expectedOperation));
    }

}
