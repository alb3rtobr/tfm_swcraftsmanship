package com.craftsmanship.tfm.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.grpc.servers.PersistenceInProcessGrpcServer;
import com.craftsmanship.tfm.grpc.services.OrderPersistenceService;
import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.DomainOrder;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;
import com.craftsmanship.tfm.utils.DomainConversion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class OrderPersistenceGrpcTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPersistenceGrpcTest.class);

    private static final String GRPC_SERVER_NAME = "test";

    private PersistenceInProcessGrpcServer orderPersistenceGrpcServer;
    private OrderPersistenceService orderPersistenceService;
    private OrderPersistenceGrpc grpcClient;
    private OrderPersistenceStub orderPersistenceStub;
    private ItemPersistenceStub itemPersistenceStub;
    private DomainConversion domainConversion;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        // Create the Persistence stubs
        itemPersistenceStub = new ItemPersistenceStub();
        orderPersistenceStub = new OrderPersistenceStub(itemPersistenceStub);
        domainConversion = new DomainConversion();

        // create the Order Grpc service
        orderPersistenceService = new OrderPersistenceService(orderPersistenceStub, domainConversion);

        orderPersistenceGrpcServer = new PersistenceInProcessGrpcServer(GRPC_SERVER_NAME, orderPersistenceService);
        orderPersistenceGrpcServer.start();
        ManagedChannel channel = InProcessChannelBuilder.forName(GRPC_SERVER_NAME).directExecutor()
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build();
        grpcClient = new OrderPersistenceGrpc(channel);
    }

    @After
    public void tearDown() throws InterruptedException {
        grpcClient.close();
        orderPersistenceGrpcServer.stop();
    }

    @Test
    public void test_given_order_with_several_existing_items_when_created_then_is_persisted()
            throws InterruptedException, ItemAlreadyExists, ItemDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainItem item2 = new DomainItem.Builder().withName("XBOX").withPrice(143).build();
        DomainItem item3 = new DomainItem.Builder().withName("SWITCH").withStock(13).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);
        DomainItem createdItem2 = itemPersistenceStub.create(item2);
        DomainItem createdItem3 = itemPersistenceStub.create(item3);

        DomainOrder order = new DomainOrder.Builder().addItem(createdItem1, 5).addItem(createdItem2, 9).addItem(createdItem3, 1).build();

        // When
        DomainOrder createdOrder = grpcClient.create(order);

        // Then
        order.setId(1L);
        assertThat(createdOrder, equalTo(order));
        assertThat(orderPersistenceStub.count(), equalTo(1));
    }

    @Test
    public void test_given_order_with_non_existing_item_when_created_then_exception() throws ItemDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainOrder order = new DomainOrder.Builder().addItem(item1, 5).build();

        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + 0 + " does not exist");

        // When
        grpcClient.create(order);
    }

    @Test
    public void test_given_several_orders_persisted_when_listed_then_orders_returned()
            throws ItemAlreadyExists, ItemDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainItem item2 = new DomainItem.Builder().withName("XBOX").withPrice(143).build();
        DomainItem item3 = new DomainItem.Builder().withName("SWITCH").withStock(13).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);
        DomainItem createdItem2 = itemPersistenceStub.create(item2);
        DomainItem createdItem3 = itemPersistenceStub.create(item3);

        DomainOrder order1 = new DomainOrder.Builder().addItem(createdItem1, 5).addItem(createdItem2, 9).addItem(createdItem3, 1).build();
        DomainOrder order2 = new DomainOrder.Builder().addItem(createdItem2, 1).addItem(createdItem3, 8).build();
        DomainOrder order3 = new DomainOrder.Builder().addItem(createdItem3, 10).build();

        orderPersistenceStub.create(order1);
        orderPersistenceStub.create(order2);
        orderPersistenceStub.create(order3);

        // When
        List<DomainOrder> orders = grpcClient.list();

        // Then
        List<DomainOrder> expectedOrders = new ArrayList<DomainOrder>();
        expectedOrders.add(order1);
        expectedOrders.add(order2);
        expectedOrders.add(order3);
        assertThat(orders, equalTo(expectedOrders));
    }

    @Test
    public void test_given_order_persisted_when_get_then_order_returned()
            throws ItemAlreadyExists, ItemDoesNotExist, OrderDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainItem item2 = new DomainItem.Builder().withName("XBOX").withPrice(143).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);
        DomainItem createdItem2 = itemPersistenceStub.create(item2);

        DomainOrder order = new DomainOrder.Builder().addItem(createdItem1, 5).addItem(createdItem2, 9).build();

        DomainOrder createdOrder = orderPersistenceStub.create(order);

        // When
        DomainOrder gotOrder = grpcClient.get(createdOrder.getId());

        // Then
        assertThat(gotOrder, equalTo(createdOrder));
    }

    @Test
    public void test_when_get_order_does_not_exist_then_exception() throws ItemAlreadyExists, ItemDoesNotExist, OrderDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);
        exceptionRule.expectMessage("Order with id " + id + " does not exist");

        // When
        grpcClient.get(id);
    }

    @Test
    public void test_given_order_persisted_when_updated_then_updated_order_returned() throws ItemAlreadyExists, ItemDoesNotExist, OrderDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainItem item2 = new DomainItem.Builder().withName("XBOX").withPrice(143).build();
        DomainItem item3 = new DomainItem.Builder().withName("SWITCH").withStock(13).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);
        DomainItem createdItem2 = itemPersistenceStub.create(item2);
        DomainItem createdItem3 = itemPersistenceStub.create(item3);

        DomainOrder order = new DomainOrder.Builder().addItem(createdItem1, 5).build();
        DomainOrder createdOrder = orderPersistenceStub.create(order);

        DomainOrder newOrder = new DomainOrder.Builder().addItem(createdItem1, 5).addItem(createdItem2, 9).addItem(createdItem3, 1).build();

        // When
        DomainOrder updatedOrder = grpcClient.update(createdOrder.getId(), newOrder);

        // Then
        newOrder.setId(createdOrder.getId());
        assertThat(updatedOrder, equalTo(newOrder));
    }

    @Test
    public void test_when_updated_and_id_does_not_exist_then_exception() throws OrderDoesNotExist, ItemDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);
        exceptionRule.expectMessage("Order with id " + id + " does not exist");

        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();
        DomainOrder order = new DomainOrder.Builder().addItem(item1, 5).build();

        // When
        grpcClient.update(id, order);
    }

    @Test
    public void test_given_order_persisted_when_updated_with_non_existing_item_then_exception() throws ItemAlreadyExists, ItemDoesNotExist, OrderDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);

        DomainOrder order = new DomainOrder.Builder().addItem(createdItem1, 5).build();
        DomainOrder createdOrder = orderPersistenceStub.create(order);

        DomainItem item2 = new DomainItem.Builder().withName("XBOX").withPrice(143).build();
        DomainOrder newOrder = new DomainOrder.Builder().addItem(createdItem1, 5).addItem(item2, 9).build();

        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + item2.getId() + " does not exist");

        // When
        grpcClient.update(createdOrder.getId(), newOrder);
    }

    @Test
    public void test_given_order_persisted_when_deleted_then_deleted() throws ItemAlreadyExists, ItemDoesNotExist, OrderDoesNotExist {
        // Given
        DomainItem item1 = new DomainItem.Builder().withName("PS4").withPrice(200).withStock(5).build();

        DomainItem createdItem1 = itemPersistenceStub.create(item1);

        DomainOrder order = new DomainOrder.Builder().addItem(createdItem1, 5).build();
        DomainOrder createdOrder = orderPersistenceStub.create(order);

        // When
        DomainOrder deletedOrder = grpcClient.delete(createdOrder.getId());

        // Then
        assertThat(deletedOrder, equalTo(createdOrder));
    }

    @Test
    public void test_when_delete_and_id_does_not_exist_then_exception() throws OrderDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);
        exceptionRule.expectMessage("Order with id " + id + " does not exist");

        // When
        grpcClient.delete(id);
    }
}
