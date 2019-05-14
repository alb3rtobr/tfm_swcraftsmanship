package com.craftsmanship.tfm.persistence;

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
import com.craftsmanship.tfm.testing.persistence.ItemPersistenceStub;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class ItemsPersistenceGrpcTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsPersistenceGrpcTest.class);
    private ItemPersistenceInProcessServer itemPersistenceGrpcServer;
    private ItemPersistenceGrpc grpcClient;
    private ItemPersistenceServiceBlockingStub blockingStub;
    private ItemPersistenceServiceStub asyncStub;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        // Create the Item Persistence stub
        ItemPersistenceStub itemPersistenceStub = new ItemPersistenceStub(); 
        itemPersistenceGrpcServer = new ItemPersistenceInProcessServer(itemPersistenceStub);
        itemPersistenceGrpcServer.start();
        ManagedChannel channel = InProcessChannelBuilder
            .forName("test")
            .directExecutor()
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext(true)
            .build();
        grpcClient = new ItemPersistenceGrpc(channel);
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

    @Test
    public void test_when_updated_is_queried_over_non_existing_id_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        Item newItem = new Item.Builder().withDescription("Shoe").build();

        grpcClient.update(id, newItem);
    }

    @Test
    public void test_when_delete_existing_item_then_item_is_deleted() {
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        grpcClient.create(item2);

        Item deletedItem = grpcClient.delete(itemResponse1.getId());

        assertThat(deletedItem, equalTo(itemResponse1));
    }

    @Test
    public void test_when_delete_non_existing_id_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        grpcClient.delete(id);
    }

    @Test
    public void test_given_no_items_when_count_then_zero() {
        assertThat(0, equalTo(grpcClient.count()));
    }

    @Test
    public void test_given_some_items_when_count_then_number_of_items_returned() {
        Item item1 = new Item.Builder().withDescription("Shoe").build();
        grpcClient.create(item1);
        Item item2 = new Item.Builder().withDescription("Car").build();
        grpcClient.create(item2);

        assertThat(2, equalTo(grpcClient.count()));
    }
}