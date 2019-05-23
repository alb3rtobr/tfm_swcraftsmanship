package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.models.Item;

public interface ItemPersistence {
    public Item create(Item item) throws CustomException;
    public List<Item> list() throws CustomException;
    public Item get(Long id) throws ItemDoesNotExist, CustomException;
    public Item update(Long id, Item item) throws ItemDoesNotExist, CustomException;
    public Item delete(Long id) throws ItemDoesNotExist, CustomException;
}