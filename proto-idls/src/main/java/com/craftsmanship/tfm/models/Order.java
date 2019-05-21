package com.craftsmanship.tfm.models;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private List<ItemPurchase> itemPurchases;

    public Order() {
        this.itemPurchases = new ArrayList<ItemPurchase>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void add(ItemPurchase itemPurchase) {
        itemPurchases.add(itemPurchase);
    }

    // TODO: Probably this should be more stylish to use iterator
    public List<ItemPurchase> getItemPurchases() {
        return itemPurchases;
    }
}