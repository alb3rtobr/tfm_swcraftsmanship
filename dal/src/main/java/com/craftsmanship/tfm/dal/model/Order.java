package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonManagedReference
    @OneToMany(mappedBy = "key.order")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    protected Order() {

    }

    public Order(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order [order id=" + id  + 
                ", ordered items=" + orderItems + "]";
    }
}
