package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.persistence.ItemPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemPersistenceStub implements ItemPersistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPersistenceStub.class);
    private Map<Long, DomainItem> items = new HashMap<Long, DomainItem>();
    private Long currentIndex = 1L;

    public DomainItem create(DomainItem item) throws ItemAlreadyExists {
        if (items.get(item.getId()) != null) {
            throw new ItemAlreadyExists(item.getName());
        }

        DomainItem newItem = new DomainItem(item);

        newItem.setId(currentIndex);
        items.put(currentIndex, newItem);
        currentIndex++;
        return newItem;
    }

    public List<DomainItem> list() {
        return new ArrayList<DomainItem>(items.values());
    }

    public DomainItem get(Long id) throws ItemDoesNotExist {
        DomainItem item = items.get(id);

        if (item == null) {
            throw new ItemDoesNotExist(id);
        }

        return item;
    }

    public DomainItem update(Long id, DomainItem item) throws ItemDoesNotExist {
        if (items.get(id) == null) {
            throw new ItemDoesNotExist(id);
        }

        item.setId(id);
        items.put(id, item);
        return item;
    }

    public DomainItem delete(Long id) throws ItemDoesNotExist {
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