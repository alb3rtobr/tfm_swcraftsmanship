package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.DomainItemPurchase;
import com.craftsmanship.tfm.models.DomainOrder;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.models.DomainOrder.Builder;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders")
public class EntityOrder  implements Order{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "key.order")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();


    protected EntityOrder() {

    }

    public EntityOrder(long id) {
        this.id = id;
    }

    public EntityOrder(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void add(ItemPurchase orderItem) {
        orderItems.add((OrderItem) orderItem);
    }

    @Override
    public List<ItemPurchase> getItemPurchases() {
        return null;// new ArrayList<ItemPurchase>(orderItems);
    }

    public List<ItemPurchase> getOrderItems() {
        return getItemPurchases();
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
        private List<OrderItem> orderItems;

        public Builder() {
            this.id = -1L;
            this.orderItems = new ArrayList<OrderItem>();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder addItem(EntityOrder order, EntityItem item, int stock) {
            orderItems.add(new OrderItem(order, item, stock));
            return this;
        }

        public EntityOrder build() {
            EntityOrder order = new EntityOrder(this.orderItems);
            order.setId(this.id);
            return order;
        }
    }

}
