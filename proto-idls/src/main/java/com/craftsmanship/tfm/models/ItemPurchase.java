package com.craftsmanship.tfm.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemPurchase)) {
            return false;
        }
        ItemPurchase itemPurchase = (ItemPurchase) o;
        return Objects.equals(item, itemPurchase.item) && quantity == itemPurchase.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity);
    }

    @Override
    public String toString() {
        return "{" + " item='" + this.item + "'" + ", quantity='" + this.quantity + "'" + "}";
    }
}