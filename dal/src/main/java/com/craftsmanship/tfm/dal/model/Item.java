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
    private int stock;

    protected Item() {

    }

    public Item(String name, long price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Item(long id, String name, long price, int stock) {
        this(name, price, stock);
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Item [name=" + name  + 
                ", price=" + price +
                ", stock=" + stock + "]";
    }
}
