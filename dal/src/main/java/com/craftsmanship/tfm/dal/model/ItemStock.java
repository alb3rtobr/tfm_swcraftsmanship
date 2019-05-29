package com.craftsmanship.tfm.dal.model;

public class ItemStock {
    private EntityItem item;
    private int stock;

    public ItemStock(EntityItem item, int stock) {
        this.item = item;
        this.stock = stock;
    }

    public EntityItem getItem() {
        return this.item;
    }

    public int getStock() {
        return this.stock;
    }
}