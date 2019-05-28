package com.craftsmanship.tfm.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainOrder {
    private Long id;
    private List<ItemPurchase> itemPurchases;

    public DomainOrder() {
        this.id = -1L;
        this.itemPurchases = new ArrayList<ItemPurchase>();
    }

    public DomainOrder(List<ItemPurchase> itemPurchases) {
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

    // TODO: Probably this should be more stylish to use iterator
    public List<ItemPurchase> getItemPurchases() {
        return itemPurchases;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DomainOrder)) {
            return false;
        }
        DomainOrder order = (DomainOrder) o;
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

        public Builder addItem(DomainItem item, int stock) {
            itemPurchases.add(new ItemPurchase(item, stock));
            return this;
        }

        public DomainOrder build() {
            DomainOrder order = new DomainOrder(this.itemPurchases);
            order.setId(this.id);

            return order;
        }
    }
}