package com.craftsmanship.tfm.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc.ItemPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v2.ItemPersistenceServiceGrpc.ItemPersistenceServiceStub;
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

public class ItemPersistenceGrpcTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceGrpcTest.class);
    private ItemPersistenceInProcessServer itemPersistenceGrpcServer;
    private ItemPersistenceGrpc grpcClient;
    private ItemPersistenceStub itemPersistenceStub;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws IOException, InstantiationException, IllegalAccessException {
        // Create the Item Persistence stub
        itemPersistenceStub = new ItemPersistenceStub();
        itemPersistenceGrpcServer = new ItemPersistenceInProcessServer(itemPersistenceStub);
        itemPersistenceGrpcServer.start();
        ManagedChannel channel = InProcessChannelBuilder.forName("test").directExecutor()
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext(true).build();
        grpcClient = new ItemPersistenceGrpc(channel);
    }

    @After
    public void tearDown() throws InterruptedException {
        grpcClient.close();
        itemPersistenceGrpcServer.stop();
    }

    @Test
    public void test_when_item_is_created() throws InterruptedException, ItemAlreadyExists {
        Item item = new Item.Builder().withName("Shoe").withPrice(2L).withQuantity(100L).build();

        Item createdItem = grpcClient.create(item);

        item.setId(1L);
        assertThat(createdItem, equalTo(item));
        assertThat(itemPersistenceStub.count(), equalTo(1));
    }

    @Test
    public void test_given_zero_items_when_list_is_queried_then_no_items_received() {
        List<Item> items = grpcClient.list();

        assertThat(items, is(empty()));
    }

    @Test
    public void test_given_some_items_when_list_is_queried_then_items_received() throws ItemAlreadyExists {
        Item item1 = new Item.Builder().withName("Shoe").withPrice(2L).build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withName("Car").withQuantity(100L).build();
        Item itemResponse2 = grpcClient.create(item2);

        List<Item> items = grpcClient.list();

        List<Item> expectedList = new ArrayList<Item>();
        expectedList.add(itemResponse1);
        expectedList.add(itemResponse2);

        assertThat(items, equalTo(expectedList));
    }

    @Test
    public void test_given_some_items_when_get_is_queried_then_item_received() throws ItemDoesNotExist, ItemAlreadyExists {
        Item item1 = new Item.Builder().withName("Shoe").withQuantity(100L).withPrice(2L).build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withName("Car").withPrice(10L).build();
        grpcClient.create(item2);

        Item responseItem = grpcClient.get(1L);

        assertThat(responseItem, equalTo(itemResponse1));
    }

    @Test
    public void test_when_get_is_queried_with_id_that_does_not_exist_then_exception() throws ItemDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        grpcClient.get(id);
    }

    @Test
    public void test_given_item_when_updated_is_queried_then_item_is_updated() throws ItemDoesNotExist, ItemAlreadyExists {
        Item item1 = new Item.Builder().withName("Shoe").withPrice(8L).build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withName("Car").build();

        Item responseItem = grpcClient.update(itemResponse1.getId(), item2);

        item2.setId(itemResponse1.getId());
        assertThat(responseItem, equalTo(item2));
    }

    @Test
    public void test_when_updated_is_queried_over_non_existing_id_then_exception() throws ItemDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        Item newItem = new Item.Builder().withName("Shoe").build();

        grpcClient.update(id, newItem);
    }

    @Test
    public void test_when_delete_existing_item_then_item_is_deleted() throws ItemDoesNotExist, ItemAlreadyExists {
        Item item1 = new Item.Builder().withName("Shoe").build();
        Item itemResponse1 = grpcClient.create(item1);
        Item item2 = new Item.Builder().withName("Car").build();
        grpcClient.create(item2);

        Item deletedItem = grpcClient.delete(itemResponse1.getId());

        assertThat(deletedItem, equalTo(itemResponse1));
    }

    @Test
    public void test_when_delete_non_existing_id_then_exception() throws ItemDoesNotExist {
        Long id = 1000L;
        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + id + " does not exist");

        grpcClient.delete(id);
    }
}
