package com.craftsmanship.tfm.models;

import java.util.List;

public interface Order {

    Long getId();

    void setId(Long id);

    void add(ItemPurchase itemPurchase);

    List<ItemPurchase> getItemPurchases();

    boolean equals(Object o);

    int hashCode();

    String toString();

}