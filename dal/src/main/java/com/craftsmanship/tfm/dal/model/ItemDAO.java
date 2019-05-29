package com.craftsmanship.tfm.dal.model;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;

@Component
public class ItemDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDAO.class);

    @Autowired
    private ItemRepository itemRepository;

    public EntityItem create(EntityItem item) throws ItemAlreadyExists {
        if (itemRepository.existsById(item.getId())) {
            // TODO: we should add here the item name
            throw new ItemAlreadyExists(item.getName());
        }

        return itemRepository.save(item);
    }

    public List<EntityItem> list() {
        return itemRepository.findAll();
    }

    public EntityItem get(Long id) throws ItemDoesNotExist {
        try {
            return itemRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ItemDoesNotExist(id);
        }
    }

    public EntityItem update(Long id, EntityItem item) {
        itemRepository.findById(id).get();  // this throws NoSuchElementException if no item with id
        item.setId(id);
        return itemRepository.save(item);
    }

    public EntityItem delete(Long id) throws ItemDoesNotExist {
        try {
            EntityItem deletedItem = itemRepository.findById(id).get();
            itemRepository.delete(deletedItem);
            return deletedItem;
        } catch (NoSuchElementException e) {
            throw new ItemDoesNotExist(id);
        }
    }

    public int count() {
        return (int) itemRepository.count();
    }
}
