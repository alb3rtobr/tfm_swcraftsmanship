package com.craftsmanship.tfm.models;

public class ItemPurchase {
    private Item item;
    private Long quantity;

    public ItemPurchase(Item item, Long quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return this.item;
    }

    public Long getQuantity() {
        return this.quantity;
    }
}