package com.craftsmanship.tfm.stockchecker.grpc;

import com.craftsmanship.tfm.models.Item;

public interface ItemsPersistence {
    public int count();
    public Item create(Item item);
}
