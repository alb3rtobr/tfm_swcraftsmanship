package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.models.Item;

public interface ItemPersistence {
    public Item create(Item item);
    public List<Item> list();
    public Item get(Long id);
    public Item update(Long id, Item item);
    public Item delete(Long id);
    public int count();
}