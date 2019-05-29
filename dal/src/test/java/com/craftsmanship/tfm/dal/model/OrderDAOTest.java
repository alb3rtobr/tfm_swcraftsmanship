package com.craftsmanship.tfm.dal.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.dal.model.ItemDAO;
import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.idls.v1.ItemPersistence;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDAOTest.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDAO dao;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        this.createSomeItems();
    }

    @After
    public void tearDown() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    private void createSomeItems() {
        Item item1 = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        Item item2 = new EntityItem.Builder().withName("Porsche").withPrice(33330).withStock(7).build();
        Item item3 = new EntityItem.Builder().withName("Lamborghini").withPrice(800000).withStock(3).build();

        itemRepository.save((EntityItem) item1);
        itemRepository.save((EntityItem) item2);
        itemRepository.save((EntityItem) item3);
    }

    public EntityItem getItem(int id) {
        return itemRepository.getOne(new Long(id));
    }

    @Test
    public void given_order_when_created_then_order_is_persisted() throws Exception {
        LOGGER.info("TENEMOS ESTOS ITEMS: " + itemRepository.findAll());
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();

        LOGGER.info("Llamamos al DAO");
        Order createdOrder = dao.create(order);

        assertThat(createdOrder, equalTo(itemRepository.getOne(createdOrder.getId())));
    }

    @Test
    public void given_order_with_non_persisted_item_when_created_then_exception() throws Exception {
        EntityItem nonPersistedItem = new EntityItem.Builder().withName("Seat").withPrice(200).withStock(1).build();
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(nonPersistedItem, 4).build();

        exceptionRule.expect(ItemDoesNotExist.class);
        exceptionRule.expectMessage("Item with id " + 0 + " does not exist");

        dao.create(order);
    }

    @Test
    public void given_order_with_no_items_when_created_then_exception() {
        // TODO: it this allowed? or exception?
    }

    @Test
    public void given_several_persisted_orders_when_list_then_all_orders_are_returned() {
        Order order1 = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        Order order2 = new EntityOrder.Builder().addItem(getItem(3), 40).build();
        Order order3 = new EntityOrder.Builder().addItem(getItem(2), 1).addItem(getItem(3), 41).build();

        orderRepository.save((EntityOrder) order1);
        orderRepository.save((EntityOrder) order2);
        orderRepository.save((EntityOrder) order3);

        List<Order> orders = dao.list();

        assertThat(orders, equalTo(orderRepository.findAll()));
    }

    @Test
    public void test_given_order_persisted_when_get_then_order_returned() throws Exception {
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        Order persistedOrder = orderRepository.save((EntityOrder) order);

        Order receivedOrder = dao.get(persistedOrder.getId());
        
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
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        Order persistedOrder = orderRepository.save((EntityOrder) order);

        Order newOrder = new EntityOrder.Builder().addItem(getItem(2), 10).build();

        Order orderReceived = dao.update(persistedOrder.getId(), newOrder);

        newOrder.setId(orderReceived.getId());
        assertThat(orderReceived, equalTo(newOrder));
    }

    @Test
    public void when_update_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        Order newOrder = new EntityOrder.Builder().addItem(getItem(2), 10).build();

        dao.update(id, newOrder);
    }

    @Test
    public void test_given_order_persisted_when_updated_with_non_existing_item_then_exception() throws Exception {
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        Order persistedOrder = orderRepository.save((EntityOrder) order);

        EntityItem nonPersistedItem = new EntityItem.Builder().withName("Seat").withPrice(200).withStock(1).build();
        Order newOrder = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(nonPersistedItem, 4).build();

        exceptionRule.expect(NoSuchElementException.class);

        dao.update(persistedOrder.getId(), newOrder);
    }

    @Test
    public void given_persisted_order_when_deleted_then_order_is_deleted() throws Exception {
        Long expectedOrders = orderRepository.count();
        Order order = new EntityOrder.Builder().addItem(getItem(1), 10).addItem(getItem(2), 4).build();
        Order persistedOrder = orderRepository.save((EntityOrder) order);

        Order deletedOrder = dao.delete(persistedOrder.getId());

        assertThat(deletedOrder, equalTo(persistedOrder));
        assertThat(orderRepository.count(), equalTo(expectedOrders));
    }

    @Test
    public void when_delete_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        dao.delete(id);
    }
}