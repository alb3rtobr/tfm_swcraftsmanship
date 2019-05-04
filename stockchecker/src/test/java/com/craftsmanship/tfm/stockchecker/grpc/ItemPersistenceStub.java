package com.craftsmanship.tfm.stockchecker.grpc;

import java.util.HashMap;
import java.util.Map;

import com.craftsmanship.tfm.models.Item;

public class ItemPersistenceStub implements ItemsPersistence {
    private static Map<Long, Item> items = new HashMap<Long, Item>();
    private static Long currentIndex = 1L;

    public Item create(Item item) {
        item.setId(currentIndex);
        items.put(currentIndex, item);
        currentIndex++;
        return item;
    }

    @Override
    public int count() {
    	return items.size();
    }
}