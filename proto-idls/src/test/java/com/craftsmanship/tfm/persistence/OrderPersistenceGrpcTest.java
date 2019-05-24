package com.craftsmanship.tfm.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.grpc.servers.PersistenceInProcessGrpcServer;
import com.craftsmanship.tfm.grpc.services.OrderPersistenceService;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceStub;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;

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

    private PersistenceInProcessGrpcServer orderPersistenceGrpcServer;
    private OrderPersistenceService orderPersistenceService;
    private OrderPersistenceGrpc grpcClient;
    private OrderPersistenceServiceBlockingStub blockingStub;
    private OrderPersistenceServiceStub asyncStub;
    private OrderPersistenceStub orderPersistenceStub;
    private ItemPersistenceStub itemPersistenceStub;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        // Create the Persistence stubs
        itemPersistenceStub = new ItemPersistenceStub();
        orderPersistenceStub = new OrderPersistenceStub(itemPersistenceStub);

        // create the Order Grpc service
        orderPersistenceService = new OrderPersistenceService(orderPersistenceStub);

        orderPersistenceGrpcServer = new PersistenceInProcessGrpcServer(orderPersistenceService);
        orderPersistenceGrpcServer.start();
        ManagedChannel channel = InProcessChannelBuilder.forName("test").directExecutor()
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext(true).build();
        grpcClient = new OrderPersistenceGrpc(channel);
        blockingStub = OrderPersistenceServiceGrpc.newBlockingStub(channel);
        asyncStub = OrderPersistenceServiceGrpc.newStub(channel);
    }

    @After
    public void tearDown() throws InterruptedException, CustomException {
        grpcClient.close();
        orderPersistenceGrpcServer.stop();
    }

    // @Test
    // public void test_given_order_with_several_items_when_create_then_is_persisted() throws InterruptedException, CustomException {
    //     // Given
    //     Item item1 = new Item.Builder().withName("PS4").withPrice(2L).withQuantity(100L).build();
    //     Item item2 = new Item.Builder().withName("XBOX").withPrice(2L).withQuantity(100L).build();
    //     Item item3 = new Item.Builder().withName("SWITCH").withPrice(2L).withQuantity(100L).build();

    //     itemPersistenceStub.create(item1);
    //     itemPersistenceStub.create(item2);
    //     itemPersistenceStub.create(item3);

    //     Order order = new Order.Builder().addItem(item1, 5).addItem(item2, 9).addItem(item3, 1).build();

    //     Order createdOrder = grpcClient.create(order);

    //     System.out.println("createdOrder = " + createdOrder);

    //     order.setId(1L);
    //     assertThat(createdOrder, equalTo(order));
    //     assertThat(orderPersistenceStub.count(), equalTo(1));
    // }

    // test_given_

}
