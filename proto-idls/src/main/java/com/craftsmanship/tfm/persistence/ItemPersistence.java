package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.ItemAlreadyExists;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.Item;

public interface ItemPersistence {
    public Item create(Item item) throws ItemAlreadyExists;
    public List<Item> list();
    public Item get(Long id) throws ItemDoesNotExist;
    public Item update(Long id, Item item) throws ItemDoesNotExist;
    public Item delete(Long id) throws ItemDoesNotExist;
}