package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
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
}
