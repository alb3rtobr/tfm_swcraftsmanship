package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
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

    @OneToMany(mappedBy = "entityOrder")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    protected EntityOrder() {

    }

    public EntityOrder(long id) {
        this.id = id;
    }

    public EntityOrder(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void add(OrderItem orderItem) {
        orderItems.add((OrderItem) orderItem);
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }


    @Override
    public String toString() {
        return "Order [order id=" + id  + 
                ", ordered items=" + orderItems + "]";
    }

    public static class Builder {

        private Long id;
        private List<ItemStock> itemStocks;

        public Builder() {
            this.id = -1L;
            this.itemStocks = new ArrayList<ItemStock>();
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
            EntityOrder order = new EntityOrder(this.id);

            for (ItemStock itemStock : this.itemStocks) {
                order.add(new OrderItem(order, itemStock.getItem(), itemStock.getStock()));
            }

            return order;
        }
    }

}
