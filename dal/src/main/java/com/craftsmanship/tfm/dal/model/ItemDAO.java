package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.persistence.ItemPersistence;

@Component
public class ItemDAO implements ItemPersistence{

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public EntityItem create(Item item) {
        return itemRepository.save((EntityItem) item);
    }

    @Override
    public List<Item> list() {
        return new ArrayList<Item>(itemRepository.findAll());
    }

    @Override
    public EntityItem get(Long id) {
        return itemRepository.findById(id).get();
    }

    @Override
    public EntityItem update(Long id, Item item) {
        return itemRepository.save((EntityItem) item);
    }

    @Override
    public EntityItem delete(Long id) {
        EntityItem deletedItem = itemRepository.findById(id).get();
        itemRepository.delete(deletedItem);
        return deletedItem;
    }

    public int count() {
        return (int) itemRepository.count();
    }
}
