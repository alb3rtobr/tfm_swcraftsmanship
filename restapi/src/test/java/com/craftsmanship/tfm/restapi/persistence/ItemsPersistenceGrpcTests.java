package com.craftsmanship.tfm.restapi.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v1.ItemPersistenceServiceGrpc.ItemPersistenceServiceStub;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.testing.grpc.ItemPersistenceInProcessServer;
import com.craftsmanship.tfm.testing.persistence.ItemsPersistenceStub;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class ItemsPersistenceGrpcTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsPersistenceGrpcTests.class);
    private ItemPersistenceInProcessServer itemPersistenceGrpcServer;
    private ItemsPersistenceGrpc grpcClient;
    private ItemPersistenceServiceBlockingStub blockingStub;
    private ItemPersistenceServiceStub asyncStub;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        // Create the Item Persistence stub
        ItemsPersistenceStub itemPersistenceStub = new ItemsPersistenceStub(); 
        itemPersistenceGrpcServer = new ItemPersistenceInProcessServer(itemPersistenceStub);
        itemPersistenceGrpcServer.start();
        ManagedChannel channel = InProcessChannelBuilder
            .forName("test")
            .directExecutor()
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext(true)
            .build();
        grpcClient = new ItemsPersistenceGrpc(channel);
        blockingStub = ItemPersistenceServiceGrpc.newBlockingStub(channel);
        asyncStub = ItemPersistenceServiceGrpc.newStub(channel);
    }

    @After
    public void tearDown() throws InterruptedException {
        grpcClient.close();
        itemPersistenceGrpcServer.stop();
    }

    @Test
    public void test_when_item_is_created() throws InterruptedException {
        Item item = new Item.Builder().withDescription("Shoe").build();

        Item createdItem = grpcClient.create(item);
        int count = grpcClient.count();

        item.setId(1L);
        assertThat(createdItem, equalTo(item));
        assertThat(count, equalTo(1));
    }

    @Test
    public void test_given_zero_items_when_list_is_queried_then_no_items_received() {
        List<Item> items = grpcClient.list();

        assertThat(items, is(empty()));
    }

    @Test
    public void test_given_some_items_when_list_is_queried_then_items_received() {
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        Item itemResponse2 = grpcClient.create(item2);

        List<Item> items = grpcClient.list();

        List<Item> expectedList = new ArrayList<Item>();
        expectedList.add(itemResponse1);
        expectedList.add(itemResponse2);

        assertThat(items, equalTo(expectedList));
    }

    @Test
    public void test_given_some_items_when_get_is_queried_then_item_received() {
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        grpcClient.create(item2);

        Item responseItem = grpcClient.get(1L);

        assertThat(responseItem, equalTo(itemResponse1));
    }

    @Test
    public void test_when_get_is_queried_with_id_that_does_not_exist_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        grpcClient.get(id);
    }

    @Test
    public void test_given_item_when_updated_is_queried_then_item_is_updated() {
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();

        Item responseItem = grpcClient.update(itemResponse1.getId(), item2);

        item2.setId(itemResponse1.getId());
        assertThat(responseItem, equalTo(item2));
    }

    // @Test
    // public void test_when_updated_is_queried_over_non_existing_id_then_item_is_created() {
    //     // TODO
    //     try {
    //         Thread.sleep(1000);
    //     } catch (InterruptedException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }
}