package com.craftsmanship.tfm.dal.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.NoSuchElementException;

import com.craftsmanship.tfm.dal.model.ItemDAO;
import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;

import org.junit.After;
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

    @After
    public void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    public void given_item_when_created_then_item_is_persisted() throws Exception {
        EntityItem item = new EntityItem.Builder().withName("BMW").withPrice(20).withStock(1).build();

        EntityItem createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_item_without_price_when_created_then_item_is_persisted() throws Exception {
        EntityItem item = new EntityItem.Builder().withName("BMW").withStock(1).build();

        EntityItem createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_item_without_stock_when_created_then_item_is_persisted() throws Exception {
        EntityItem item = new EntityItem.Builder().withName("BMW").withPrice(20).build();

        EntityItem createdItem = dao.create(item);

        assertThat(createdItem, equalTo(itemRepository.getOne(createdItem.getId())));
    }

    @Test
    public void given_several_persisted_items_when_list_then_all_items_are_returned() {
        EntityItem item1 = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        EntityItem item2 = new EntityItem.Builder().withName("Porsche").withPrice(33330).withStock(7).build();
        EntityItem item3 = new EntityItem.Builder().withName("Lamborghini").withPrice(800000).withStock(3).build();

        itemRepository.save((EntityItem) item1);
        itemRepository.save((EntityItem) item2);
        itemRepository.save((EntityItem) item3);

        List<EntityItem> items = dao.list();

        assertThat(items, equalTo(itemRepository.findAll()));
    }

    @Test
    public void given_persisted_item_when_get_then_item_is_returned() throws Exception {
        EntityItem item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();

        EntityItem persistedItem = itemRepository.save((EntityItem) item);

        EntityItem receivedItem = dao.get(persistedItem.getId());

        assertThat(receivedItem, equalTo(persistedItem));
    }

    @Test
    public void when_get_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(ItemDoesNotExist.class);

        dao.get(id);
    }

    @Test
    public void given_persisted_item_when_updated_then_item_is_returned() {
        EntityItem item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        EntityItem persistedItem = itemRepository.save((EntityItem) item);

        EntityItem newItem = new EntityItem.Builder().withName("BMW").withPrice(100000).withStock(10).build();

        EntityItem receivedItem = dao.update(persistedItem.getId(), newItem);

        newItem.setId(persistedItem.getId());
        assertThat(receivedItem, equalTo(newItem));
    }

    @Test
    public void when_update_id_does_not_exist_then_exception() {
        Long id = 1000L;
        exceptionRule.expect(NoSuchElementException.class);

        EntityItem newItem = new EntityItem.Builder().withName("BMW").withPrice(100000).withStock(10).build();

        dao.update(id, newItem);
    }

    @Test
    public void given_persisted_item_when_deleted_then_item_is_deleted() throws Exception {
        Long expectedItems = itemRepository.count();
        EntityItem item = new EntityItem.Builder().withName("BMW").withPrice(200000).withStock(1).build();
        EntityItem persistedItem = itemRepository.save((EntityItem) item);

        EntityItem deletedItem = dao.delete(persistedItem.getId());

        assertThat(deletedItem, equalTo(persistedItem));
        assertThat(itemRepository.count(), equalTo(expectedItems));
    }

    @Test
    public void when_delete_id_does_not_exist_then_exception() throws Exception {
        Long id = 1000L;
        exceptionRule.expect(ItemDoesNotExist.class);

        dao.delete(id);
    }
}