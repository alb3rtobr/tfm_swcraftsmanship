package com.craftsmanship.tfm.models;

import java.util.Objects;

public class ItemPurchase {
    private Item item;
    private int quantity;

    public ItemPurchase() {
        this(null, 0);
    }

    public ItemPurchase(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
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
        return Objects.equals(item, itemPurchase.item) && Objects.equals(quantity, itemPurchase.quantity);
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