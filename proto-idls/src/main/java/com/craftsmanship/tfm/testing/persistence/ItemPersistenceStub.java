package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.persistence.ItemPersistence;

public class ItemPersistenceStub implements ItemPersistence {
    private Map<Long, Item> items = new HashMap<Long, Item>();
    private Long currentIndex = 1L;

    public Item create(Item item) {
        item.setId(currentIndex);
        items.put(currentIndex, item);
        currentIndex++;
        return item;
    }

    public List<Item> list() {
        return new ArrayList<Item>(items.values());
    }

    public Item get(Long id) {
        return items.get(id);
    }

    public Item update(Long id, Item item) {
        item.setId(id);
        items.put(id, item);
        return item;
    }

    public Item delete(Long id) {
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