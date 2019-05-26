package com.craftsmanship.tfm.dal.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class OrderItem {

    @EmbeddedId
    @JsonIgnore
    private OrderItemKey key;

    private int quantity;
    
    protected OrderItem() {
    }

    public OrderItem(Order order, Item item, int quantity) {
        key = new OrderItemKey();
        key.setOrder(order);
        key.setItem(item);
        this.quantity = quantity;
    }

    @Transient
    public Item getItem() {
        return this.key.getItem();
    }

    public OrderItemKey getKey() {
        return key;
    }
    
    public void setKey(OrderItemKey key) {
        this.key = key;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderItem other = (OrderItem) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }

        return true;
    }
}
