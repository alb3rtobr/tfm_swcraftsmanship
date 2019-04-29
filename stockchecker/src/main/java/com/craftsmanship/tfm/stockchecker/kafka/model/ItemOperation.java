package com.craftsmanship.tfm.stockchecker.kafka.model;

import java.util.Objects;

import com.craftsmanship.tfm.stockchecker.kafka.model.Item;

public class ItemOperation {

    private Item item;

    public ItemOperation() {
    }

    public ItemOperation(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemOperation)) {
            return false;
        }
        ItemOperation operation = (ItemOperation) o;
        return Objects.equals(item, operation.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "{" +
            " item='" + this.item + "'" +
            "}";
    }
}
