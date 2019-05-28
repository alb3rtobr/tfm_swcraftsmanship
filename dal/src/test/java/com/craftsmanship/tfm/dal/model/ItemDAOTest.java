package com.craftsmanship.tfm.dal.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.dal.model.ItemDAO;
import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.idls.v1.ItemPersistence;

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
public class ItemDAOTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDAOTest.class);

    @Autowired
    private ItemDAO dao;

    @Autowired
    private ItemRepository itemRepository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    public void given_item_when_created_then_item_is_persisted() {
        Item item = new EntityItem.Builder().withName("BMW").withPrice(20).withStock(1).build();

        Item createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_item_without_price_when_created_then_item_is_persisted() {
        Item item = new EntityItem.Builder().withName("BMW").withStock(1).build();

        Item createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_item_without_stock_when_created_then_item_is_persisted() {
        Item item = new EntityItem.Builder().withName("BMW").withPrice(20).build();

        Item createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_several_persisted_items_when_list_then_all_items_are_returned() {
        Item item1 = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        Item item2 = new EntityItem.Builder().withName("Porsche").withPrice(33330).withStock(7).build();
        Item item3 = new EntityItem.Builder().withName("Lamborghini").withPrice(800000).withStock(3).build();

        List<Item> expectedItems = new ArrayList<Item>();
        expectedItems.add(item1);
        expectedItems.add(item2);
        expectedItems.add(item3);

        itemRepository.save((EntityItem) item1);
        itemRepository.save((EntityItem) item2);
        itemRepository.save((EntityItem) item3);

        List<Item> items = dao.list();

        assertThat(items, equalTo(itemRepository.findAll()));
    }

    @Test
    public void given_persisted_item_when_get_then_item_is_returned() {
        Item item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();

        Item persistedItem = itemRepository.save((EntityItem) item);

        Item receivedItem = dao.get(persistedItem.getId());

        assertThat(receivedItem, equalTo(persistedItem));
    }

    @Test
    public void when_get_id_does_not_exist_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        dao.get(id);
    }

    @Test
    public void given_persisted_item_when_updated_then_item_is_returned() {
        Item item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        Item persistedItem = itemRepository.save((EntityItem) item);

        Item newItem = new EntityItem.Builder().withName("BMW").withPrice(100000).withStock(10).build();

        Item receivedItem = dao.update(persistedItem.getId(), newItem);

        newItem.setId(persistedItem.getId());
        assertThat(receivedItem, equalTo(newItem));
    }

    @Test
    public void when_update_id_does_not_exist_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        Item newItem = new EntityItem.Builder().withName("BMW").withPrice(100000).withStock(10).build();

        dao.update(id, newItem);
    }

    @Test
    public void given_persisted_item_when_deleted_then_item_is_deleted() {
        Long expectedItems = itemRepository.count();
        Item item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        Item persistedItem = itemRepository.save((EntityItem) item);

        Item deletedItem = dao.delete(persistedItem.getId());

        assertThat(deletedItem, equalTo(persistedItem));
        assertThat(itemRepository.count(), equalTo(expectedItems));
    }

    @Test
    public void when_delete_id_does_not_exist_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        dao.delete(id);
    }
}