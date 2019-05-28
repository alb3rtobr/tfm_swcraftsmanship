package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.persistence.ItemPersistence;

@Component
public class ItemDAO implements ItemPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDAO.class);

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Item create(Item item) {
        LOGGER.info("Creating item: " + item);
        LOGGER.info("itemRepository: " + itemRepository);
        return itemRepository.save((EntityItem) item);
    }

    @Override
    public List<Item> list() {
        return new ArrayList<Item>(itemRepository.findAll());
    }

    @Override
    public Item get(Long id) {
        return itemRepository.findById(id).get();
    }

    @Override
    public Item update(Long id, Item item) {
        itemRepository.findById(id).get();  // this throws NoSuchElementException if no item with id
        item.setId(id);
        return itemRepository.save((EntityItem) item);
    }

    @Override
    public Item delete(Long id) {
        EntityItem deletedItem = itemRepository.findById(id).get();
        itemRepository.delete(deletedItem);
        return deletedItem;
    }

    public int count() {
        return (int) itemRepository.count();
    }
}
