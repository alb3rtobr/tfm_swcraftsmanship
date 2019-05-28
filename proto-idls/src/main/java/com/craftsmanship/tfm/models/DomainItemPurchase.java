package com.craftsmanship.tfm.models;

import java.util.Objects;

public class DomainItemPurchase implements ItemPurchase {
    private DomainItem item;
    private int quantity;

    public DomainItemPurchase() {
        this(null, 0);
    }

    public DomainItemPurchase(DomainItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public DomainItem getItem() {
        return this.item;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DomainItemPurchase)) {
            return false;
        }
        DomainItemPurchase itemPurchase = (DomainItemPurchase) o;
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