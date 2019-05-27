package com.craftsmanship.tfm.models;

import java.util.Objects;

public class ItemOperation {

    private OperationType type;
    private DomainItem item;

    public ItemOperation() {
    }

    public ItemOperation(OperationType type, DomainItem item) {
        this.type = type;
        this.item = item;
    }

    public DomainItem getItem() {
        return this.item;
    }

    public void setItem(DomainItem item) {
        this.item = item;
    }

    public OperationType getType() {
        return this.type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemOperation)) {
            return false;
        }
        ItemOperation operation = (ItemOperation) o;
        return Objects.equals(type, operation.type) && Objects.equals(item, operation.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "{" +
            " type='" + this.type + "', " +
            " item='" + this.item + "'" +
            "}";
    }
}