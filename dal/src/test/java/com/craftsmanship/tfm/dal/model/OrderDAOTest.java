package com.craftsmanship.tfm.dal.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.craftsmanship.tfm.dal.grpc.server.GrpcServer;
import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderDAOTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAOTest.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderDAO dao;
    
    private List<EntityItem> items = new ArrayList<EntityItem>();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        this.createSomeItems();
    }

    @After
    public void tearDown() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private void createSomeItems() {
        EntityItem item1 = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1000).build();
        EntityItem item2 = new EntityItem.Builder().withName("Porsche").withPrice(33330).withStock(7000).build();
        EntityItem item3 = new EntityItem.Builder().withName("Lamborghini").withPrice(800000).withStock(3000).build();

        this.items.add(0, itemRepository.save(item1));
        this.items.add(1, itemRepository.save(item2));
        this.items.add(2, itemRepository.save(item3));
        itemRepository.flush();
    }

    public EntityItem getItem(int id) {
        return items.get(id - 1);
    }

    @Test
    public void given_order_when_created_then_order_is_persisted() throws Exception {
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();

        EntityOrder createdOrder = dao.create(order);

        assertThat(createdOrder, equalTo(orderRepository.getOne(createdOrder.getId())));
    }

    @Test
    public void given_order_when_created_then_items_stock_decrease() throws Exception {
        int quantity1 = 10;
        int quantity2 = 4;
        int expectedStock1 = getItem(1).getStock() - quantity1;
        int expectedStock2 = getItem(2).getStock() - quantity2;
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), quantity1).addItem(getItem(2), quantity2).build();

        dao.create(order);

        assertThat(itemRepository.getOne(getItem(1).getId()).getStock(), equalTo(expectedStock1));
        assertThat(itemRepository.getOne(getItem(2).getId()).getStock(), equalTo(expectedStock2));
    }

    @Test
    public void given_order_with_non_persisted_item_when_created_then_exception() throws Exception {
        EntityItem nonPersistedItem = new EntityItem.Builder().withName("Seat").withPrice(200).withStock(1).build();
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(nonPersistedItem, 4).build();

        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item does not exist");

        dao.create(order);
    }

    @Test
    public void given_order_with_no_items_when_created_then_exception() {
        // TODO: it this allowed? or exception?
    }

    @Test
    public void given_order_requesting_item_with_no_enough_stock_when_created_then_exception() throws Exception {
        EntityItem itemWithLowStock = new EntityItem.Builder().withName("Seat").withPrice(200).withStock(1).build();
        EntityItem savedItem = itemRepository.saveAndFlush(itemWithLowStock);

        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(savedItem, 4).build();

        exceptionRule.expect(ItemWithNoStockAvailable.class);

        dao.create(order);
    }

    @Test
    public void given_several_persisted_orders_when_list_then_all_orders_are_returned() {
        EntityOrder order1 = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        EntityOrder order2 = new EntityOrder.Builder().addItem(getItem(3), 40).build();
        EntityOrder order3 = new EntityOrder.Builder().addItem(getItem(2), 1).addItem(getItem(3), 41).build();

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        Set<EntityOrder> orders = dao.list();

        assertThat(orders, equalTo(new HashSet<EntityOrder>(orderRepository.findAll())));
    }

    @Test
    public void test_given_order_persisted_when_get_then_order_returned() throws Exception {
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        EntityOrder persistedOrder = orderRepository.save(order);

        EntityOrder receivedOrder = dao.get(persistedOrder.getId());
        
        assertThat(receivedOrder, equalTo(persistedOrder));
    }

    @Test
    public void when_get_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);

        dao.get(id);
    }

    @Test
    public void given_persisted_order_when_updated_then_order_is_returned() throws Exception {
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();

        EntityOrder persistedOrder = dao.create(order);

        EntityOrder newOrder = new EntityOrder.Builder().addItem(getItem(2), 10).build();

        EntityOrder orderReceived = dao.update(persistedOrder.getId(), newOrder);

        newOrder.setId(orderReceived.getId());
        assertThat(orderReceived, equalTo(newOrder));
    }

    @Test
    public void when_update_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);

        EntityOrder newOrder = new EntityOrder.Builder().addItem(getItem(2), 10).build();

        dao.update(id, newOrder);
    }

    @Test
    public void test_given_order_persisted_when_updated_with_non_existing_item_then_exception() throws Exception {
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        persistOrderItems(order);
        EntityOrder persistedOrder = orderRepository.save(order);

        EntityItem nonPersistedItem = new EntityItem.Builder().withName("Seat").withPrice(200).withStock(1).build();
        EntityOrder newOrder = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(nonPersistedItem, 4).build();

        exceptionRule.expect(ItemDoesNotExist.class);

        dao.update(persistedOrder.getId(), newOrder);
    }

    @Test
    public void given_persisted_order_when_deleted_then_order_is_deleted() throws Exception {
        Long expectedOrders = orderRepository.count();
        EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        EntityOrder persistedOrder = orderRepository.save(order);

        EntityOrder deletedOrder = dao.delete(persistedOrder.getId());

        assertThat(deletedOrder, equalTo(persistedOrder));
        assertThat(orderRepository.count(), equalTo(expectedOrders));
    }

    // @Test
    // public void given_persisted_order_when_deleted_then_item_stocks_are_updated() throws Exception {
    //     int quantity1 = 10;
    //     int quantity2 = 4;
    //     int expectedStock1 = getItem(1).getStock() - quantity1;
    //     int expectedStock2 = getItem(2).getStock() - quantity2;
    //     int expectedStockDeleted1 = getItem(1).getStock();
    //     int expectedStockDeleted2 = getItem(2).getStock();

    //     EntityOrder order = new EntityOrder.Builder().addItem(getItem(1), quantity1).addItem(getItem(2), quantity2).build();
    //     EntityOrder persistedOrder = dao.create(order);

    //     assertThat(itemRepository.getOne(getItem(1).getId()).getStock(), equalTo(expectedStock1));
    //     assertThat(itemRepository.getOne(getItem(2).getId()).getStock(), equalTo(expectedStock2));

    //     dao.delete(persistedOrder.getId());

    //     assertThat(itemRepository.getOne(getItem(1).getId()).getStock(), equalTo(expectedStockDeleted1));
    //     assertThat(itemRepository.getOne(getItem(2).getId()).getStock(), equalTo(expectedStockDeleted2));
    // }

    @Test
    public void when_delete_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(OrderDoesNotExist.class);
        dao.delete(id);
    }

    private void persistOrderItems(EntityOrder order) {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            orderItemRepository.save(itemPurchase);
        }
    }

}