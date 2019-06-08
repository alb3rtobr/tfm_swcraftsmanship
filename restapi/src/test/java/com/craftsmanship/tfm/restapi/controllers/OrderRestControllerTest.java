package com.craftsmanship.tfm.restapi.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.OperationType;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.http.HttpStatus;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("dev")
public class OrderRestControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRestControllerTest.class);

    private static String SENDER_TOPIC = "item_modified";

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
    private OrderRestController orderRestController;

    @Autowired
    private OrderPersistenceStub orderPersistence;
    private ItemPersistenceStub itemPersistence;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws ItemAlreadyExists {
        // get persistence
        itemPersistence = orderPersistence.getItemPersistenceStub();

        // create some items
        this.createSomeItems();

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
                LOGGER.info("test-listener received message='{}'", record.toString());
                LOGGER.info("we have " + records.size() + " records.", record.toString());
                records.add(record);
            }
        });

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }

    @After
    public void tearDown() throws CustomException {
        // stop the container
        container.stop();

        // initialize persistence
        orderPersistence.initialize();

        // clean received messages
        records.clear();
    }

    @Test
    public void contexLoads() throws Exception {
        assertThat(orderRestController, is(notNullValue()));
    }

    private void createSomeItems() throws ItemAlreadyExists {
        Item item1 = new Item.Builder().withName("PS4").withStock(15).build();
        Item item2 = new Item.Builder().withName("Switch").withPrice(350).withStock(10).build();
        Item item3 = new Item.Builder().withName("NES").withPrice(80).build();

        itemPersistence.create(item1);
        itemPersistence.create(item2);
        itemPersistence.create(item3);
    }

    private Item getItem(int id) throws ItemDoesNotExist {
        return itemPersistence.get(new Long(id));
    }

    private ResponseEntity<Order> getOrder(Long id) throws HttpClientErrorException {
        String url = "http://localhost:" + restPort + "/api/v1/orders/" + id;
        return new RestTemplate().getForEntity(url, Order.class);
    }

    private ResponseEntity<List<Order>> listOrders() throws HttpClientErrorException {
        String url = "http://localhost:" + restPort + "/api/v1/orders/";
        return new RestTemplate().exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Order>>() {});
    }

    private ResponseEntity<Order> createOrder(Order order) throws HttpClientErrorException {
        String url = "http://localhost:" + restPort + "/api/v1/orders";
        HttpEntity<Order> request = new HttpEntity<>(order);
        return new RestTemplate().postForEntity(url, request, Order.class);
    }

    private ResponseEntity<Order> editOrder(Long id, Order order) throws HttpClientErrorException {
        String url = "http://localhost:" + restPort + "/api/v1/orders/" + id;
        HttpEntity<Order> request = new HttpEntity<>(order);
        return new RestTemplate().exchange(url, HttpMethod.PUT, request, Order.class);
    }

    private void deleteOrder(Long id) throws HttpClientErrorException {
        String url = "http://localhost:" + restPort + "/api/v1/orders/" + id;
        new RestTemplate().delete(url);
    }

    private void checkKafkaMessages(OperationType type, List<ItemPurchase> purchases) throws InterruptedException {
        List<ItemOperation> receivedItemsOperations = new ArrayList<ItemOperation>();

        // get all the Kafka messages (should be one per item purchase)
        for (int i = 0; i < purchases.size(); i++) {
            ConsumerRecord<String, ItemOperation> received = records.poll(10, TimeUnit.SECONDS);
            receivedItemsOperations.add(received.value());
        }

        // check that all the received ItemOperations are the expected ones
        for (ItemPurchase purchase : purchases) {
            ItemOperation expectedOperation = new ItemOperation(type, purchase.getItem());
            assertThat(expectedOperation, isIn(receivedItemsOperations));
        }
    }

    @Test
    public void test_when_order_is_created_then_order_persisted_and_kafka_messages_generated()
            throws Exception {

        Order order = new Order.Builder().addItem(getItem(1), 1).addItem(getItem(2), 5).addItem(getItem(3), 2).build();

        // post order
        Order responseOrder = createOrder(order).getBody();

        // first, for comparison, we need to set the order id, created in persistence
        order.setId(responseOrder.getId());

        // check returned order is the one created
        assertThat(responseOrder, equalTo(order));

        // check item was created in persistence
        Order storedOrder = orderPersistence.get(responseOrder.getId());
        assertThat(storedOrder, equalTo(order));

        // check that the Kafka message was received
        checkKafkaMessages(OperationType.CREATED, 
                (List <ItemPurchase>)(List)order.getItemPurchases());
    }

    @Test
    public void test_when_order_is_created_with_item_that_does_not_exist_then_order_is_not_created_and_no_kafka_messages()
            throws Exception {

        int numKafkaMessages = records.size();

        Item itemNotPersisted = new Item.Builder().withName("NES").withPrice(80).build();

        Order order = new Order.Builder().addItem(getItem(1), 1).addItem(getItem(2), 5).addItem(itemNotPersisted, 2).build();

        try {
            createOrder(order);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            // check error happened
            assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));

            // check no kafka messages received
            assertThat(records.size(), equalTo(numKafkaMessages));
        }
    }

    @Test
    public void test_given_some_orders_when_list_orders_then_orders_are_returned() throws Exception {
        // Given
        Order order1 = new Order.Builder().addItem(getItem(1), 1).addItem(getItem(2), 5).build();
        Order orderCreated1 = orderPersistence.create(order1);
        Order order2 = new Order.Builder().addItem(getItem(2), 67).addItem(getItem(3), 12).build();
        Order orderCreated2 = orderPersistence.create(order2);

        // When
        List<Order> receivedOrders = listOrders().getBody();

        // Then
        List<Order> expectedList = new ArrayList<Order>();
        expectedList.add(orderCreated1);
        expectedList.add(orderCreated2);
        assertThat(receivedOrders, equalTo(expectedList));
    }

    @Test
    public void test_given_order_when_get_order_then_order_is_returned() throws Exception {
        // Given
        Order order = new Order.Builder().addItem(getItem(1), 1).addItem(getItem(3), 5).build();
        Order orderCreated = orderPersistence.create(order);

        // When
        Order getOrder = getOrder(orderCreated.getId()).getBody();

        // Then
        order.setId(orderCreated.getId());
        assertThat(order, equalTo(getOrder));
    }

    @Test
    public void test_when_get_non_existing_order_then_exception() throws Exception {
        try {
            getOrder(1000L).getBody();
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            // check error happened
            assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void test_given_order_when_edit_then_edited_order_is_returned() throws Exception {
        // Given
        Order order = new Order.Builder().addItem(getItem(2), 1).addItem(getItem(3), 5).build();
        Order orderCreated = orderPersistence.create(order);

        // When
        Order newOrder = new Order.Builder().addItem(getItem(2), 1).addItem(getItem(3), 5).build();
        Order editedOrder = editOrder(orderCreated.getId(), newOrder).getBody();

        // Then
        newOrder.setId(orderCreated.getId());
        assertThat(editedOrder, equalTo(newOrder));
    }

    @Test
    public void test_given_order_when_edit_with_item_does_not_exist_then_exception() throws Exception {
        // Given
        Order order = new Order.Builder().addItem(getItem(2), 1).addItem(getItem(3), 5).build();
        Order orderCreated = orderPersistence.create(order);

        // When
        Item itemNotPersisted = new Item.Builder().withName("Show").withPrice(80).build();
        Order newOrder = new Order.Builder().addItem(getItem(2), 1).addItem(itemNotPersisted, 5).build();

        try {
            editOrder(orderCreated.getId(), newOrder);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            // check error happened
            assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void test_when_edit_order_does_not_exist_then_exception() throws Exception {
        Order newOrder = new Order.Builder().addItem(getItem(2), 1).addItem(getItem(3), 5).build();

        try {
            editOrder(1000L, newOrder);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            // check error happened
            assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void test_given_some_orders_when_delete_order_then_order_is_deleted()
            throws InterruptedException, ItemAlreadyExists, ItemDoesNotExist {
        // Given
        Order order1 = new Order.Builder().addItem(getItem(1), 1).addItem(getItem(2), 5).build();
        Order orderCreated1 = orderPersistence.create(order1);
        Order order2 = new Order.Builder().addItem(getItem(2), 67).addItem(getItem(3), 12).build();
        orderPersistence.create(order2);

        // When
        deleteOrder(orderCreated1.getId());

        try {
            orderPersistence.get(orderCreated1.getId());
            Assert.fail();
        } catch (OrderDoesNotExist e) {
        }
    }

    @Test
    public void test_when_delete_order_does_not_exist_then_exception() throws Exception {
        try {
            deleteOrder(1000L);
            Assert.fail();
        } catch (HttpClientErrorException ex) {
            // check error happened
            assertThat(ex.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        }
    }
}
