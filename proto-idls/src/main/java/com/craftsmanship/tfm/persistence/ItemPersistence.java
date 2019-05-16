package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.models.Item;

public interface ItemPersistence {
    public Item create(Item item) throws CustomException;
    public List<Item> list() throws CustomException;
    public Item get(Long id) throws CustomException;
    public Item update(Long id, Item item) throws CustomException;
    public Item delete(Long id) throws CustomException;
    public int count() throws CustomException;
}