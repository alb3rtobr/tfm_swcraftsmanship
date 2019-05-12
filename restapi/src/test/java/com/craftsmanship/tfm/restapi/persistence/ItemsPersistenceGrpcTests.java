package com.craftsmanship.tfm.restapi.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.testing.grpc.ItemPersistenceExampleServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemsPersistenceGrpcTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsPersistenceGrpcTests.class);

    private class GrpcServerRunnable implements Runnable {
        private ItemPersistenceExampleServer gRpcServer;

        public synchronized void doStop() throws InterruptedException {
            LOGGER.info("Stopping gRPC Server...");
            gRpcServer.stop();
            gRpcServer.blockUntilShutdown();
        }

        public synchronized void initialize() {
            LOGGER.info("Initializing gRPC Server...");
            gRpcServer.initialize();
        }

        @Override
        public void run() {
            gRpcServer = new ItemPersistenceExampleServer(50051);
            try {
                LOGGER.info("Starting gRPC Server...");
                gRpcServer.start();
                gRpcServer.blockUntilShutdown();
            } catch (Exception e) {
                throw new RuntimeException("Exception running gRPC Server");
            }
        }
    }

    private GrpcServerRunnable grpcServerRunnable;
    private ItemsPersistenceGrpc itemsPersistenceGrpc;

    // TODO: This should be BeforeClass
    @Before
    public void setUp() {
        grpcServerRunnable = new GrpcServerRunnable();
        Thread thread = new Thread(grpcServerRunnable);
        thread.start();

        itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
    }

    // TODO: the server stop should be AfterClass and initialize @After
    @After
    public void tearDown() throws InterruptedException {
        //TODO
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        itemsPersistenceGrpc.close();

        grpcServerRunnable.initialize();
        grpcServerRunnable.doStop();
    }

    @Test
    public void test_when_item_is_created() throws InterruptedException {
        Item item = new Item.Builder().withDescription("Shoe").build();
        Item createdItem = itemsPersistenceGrpc.create(item);
        int count = itemsPersistenceGrpc.count();

        item.setId(1L);
        assertThat(createdItem, equalTo(item));
        assertThat(count, equalTo(1));
    }

    @Test
    public void test_given_zero_items_when_list_is_queried_then_no_items_received() {
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        List<Item> items = itemsPersistenceGrpc.list();

        assertThat(items, is(empty()));
    }

    @Test
    public void test_given_some_items_when_list_is_queried_then_items_received() {
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = itemsPersistenceGrpc.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        Item itemResponse2 = itemsPersistenceGrpc.create(item2);

        List<Item> items = itemsPersistenceGrpc.list();

        List<Item> expectedList = new ArrayList<Item>();
        expectedList.add(itemResponse1);
        expectedList.add(itemResponse2);

        assertThat(items, equalTo(expectedList));
    }

    @Test
    public void test_given_some_items_when_get_is_queried_then_item_received() {
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = itemsPersistenceGrpc.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        itemsPersistenceGrpc.create(item2);

        Item responseItem = itemsPersistenceGrpc.get(1L);

        assertThat(responseItem, equalTo(itemResponse1));
    }

    public void test_when_get_is_queried_with_id_that_does_not_exist_then_error() {
        //TODO
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test_given_item_when_updated_is_queried_then_item_is_updated() {
        ItemsPersistenceGrpc itemsPersistenceGrpc = new ItemsPersistenceGrpc("localhost", 50051);
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = itemsPersistenceGrpc.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();

        Item responseItem = itemsPersistenceGrpc.update(itemResponse1.getId(), item2);

        item2.setId(itemResponse1.getId());
        assertThat(responseItem, equalTo(item2));
    }

    @Test
    public void test_when_updated_is_queried_over_non_existing_id_then_item_is_created() {
        //TODO
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}