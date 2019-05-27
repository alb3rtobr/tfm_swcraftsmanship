package com.craftsmanship.tfm.models;

public interface Item {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    Long getPrice();

    void setPrice(Long price);

    int getStock();

    void setStock(int stock);

    boolean equals(Object o);

    int hashCode();

    String toString();

}