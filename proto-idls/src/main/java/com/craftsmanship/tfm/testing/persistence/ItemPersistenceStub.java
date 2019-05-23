package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.persistence.ItemPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemPersistenceStub implements ItemPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceStub.class);
    private Map<Long, Item> items = new HashMap<Long, Item>();
    private Long currentIndex = 1L;

    public Item create(Item item) throws ItemAlreadyExists {
        if (items.get(item.getId()) != null) {
            throw new ItemAlreadyExists(item.getName());
        }

        item.setId(currentIndex);
        items.put(currentIndex, item);
        currentIndex++;
        return item;
    }

    public List<Item> list() {
        return new ArrayList<Item>(items.values());
    }

    public Item get(Long id) throws ItemDoesNotExist {
        Item item = items.get(id);

        if (item == null) {
            throw new ItemDoesNotExist(id);
        }

        return item;
    }

    public Item update(Long id, Item item) throws ItemDoesNotExist {
        if (items.get(id) == null) {
            throw new ItemDoesNotExist(id);
        }

        item.setId(id);
        items.put(id, item);
        return item;
    }

    public Item delete(Long id) throws ItemDoesNotExist {
        LOGGER.info("PERSISTENCE ITEMS: " + items);
        if (items.get(id) == null) {
            throw new ItemDoesNotExist(id);
        }

        return items.remove(id);
    }

    public int count() {
        return items.size();
    }

    public void initialize() {
        items.clear();
        currentIndex = 1L;
    }
}