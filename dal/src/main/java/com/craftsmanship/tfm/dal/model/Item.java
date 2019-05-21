package com.craftsmanship.tfm.dal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private long price;
    private long quantity;

    protected Item() {

    }

    public Item(String name, long price, long quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Item(long id, String name, long price, long quantity) {
        this(name, price, quantity);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item [name=" + name  + 
                ", price=" + price +
                ", quantity=" + quantity + "]";
    }
}
