package com.craftsmanship.tfm.models;

public interface ItemPurchase {

    Item getItem();

    int getQuantity();

    boolean equals(Object o);

    int hashCode();

    String toString();

}