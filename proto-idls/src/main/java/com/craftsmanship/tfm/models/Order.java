package com.craftsmanship.tfm.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private Long id;
    private List<ItemPurchase> itemPurchases;

    public Order() {
        this.id = -1L;
        this.itemPurchases = new ArrayList<ItemPurchase>();
    }

    public Order(List<ItemPurchase> itemPurchases) {
        this.itemPurchases = itemPurchases;
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

    public List<ItemPurchase> getItemPurchases() {
        return new ArrayList<ItemPurchase>(itemPurchases);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Order)) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(itemPurchases, order.itemPurchases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemPurchases);
    }

    @Override
    public String toString() {
        return "{" + " id='" + this.id + "'" + ", itemPurchases='" + this.itemPurchases + "'" + "}";
    }

    public static class Builder {

        private Long id;
        private List<ItemPurchase> itemPurchases;

        public Builder() {
            this.id = -1L;
            this.itemPurchases = new ArrayList<ItemPurchase>();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder addItem(Item item, int stock) {
            itemPurchases.add(new ItemPurchase(item, stock));
            return this;
        }

        public Order build() {
            Order order = new Order(this.itemPurchases);
            order.setId(this.id);

            return order;
        }
    }
}