package com.craftsmanship.tfm.dal.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class EntityOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "entityOrder", fetch = FetchType.EAGER)
    private Set<OrderItem> orderItems = new HashSet<OrderItem>();

    public EntityOrder() {
    }

    public EntityOrder(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public EntityOrder(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void add(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "EntityOrder [id=" + id + " orderItems = " + orderItems + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((orderItems == null) ? 0 : orderItems.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityOrder other = (EntityOrder) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (orderItems == null) {
            if (other.orderItems != null)
                return false;
        } else if (!orderItems.equals(other.orderItems))
            return false;
        return true;
    }

    public static class Builder {

        private Long id;
        private Set<ItemStock> itemStocks;

        public Builder() {
            this.id = -1L;
            this.itemStocks = new HashSet<ItemStock>();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder addItem(EntityItem item, int stock) {
            itemStocks.add(new ItemStock(item, stock));
            return this;
        }

        public EntityOrder build() {
            Set<OrderItem> orderItems = new HashSet<OrderItem>();
            for (ItemStock itemStock : this.itemStocks) {
                OrderItem orderItem = new OrderItem(null, itemStock.getItem(), itemStock.getStock());
                orderItems.add(orderItem);
            }

            EntityOrder order = new EntityOrder(orderItems);
            order.setId(this.id);
            return order;
        }
    }
}
