package com.craftsmanship.tfm.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DomainOrder implements Order {
    private Long id;
    private List<DomainItemPurchase> itemPurchases;

    public DomainOrder() {
        this.id = -1L;
        this.itemPurchases = new ArrayList<DomainItemPurchase>();
    }

    public DomainOrder(List<DomainItemPurchase> itemPurchases) {
        this.itemPurchases = itemPurchases;
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
    public void add(ItemPurchase itemPurchase) {
        itemPurchases.add((DomainItemPurchase) itemPurchase);
    }

    // TODO: Probably this should be more stylish to use iterator
    @Override
    public List<ItemPurchase> getItemPurchases() {
        return new ArrayList<ItemPurchase>(itemPurchases);
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
        private List<DomainItemPurchase> itemPurchases;

        public Builder() {
            this.id = -1L;
            this.itemPurchases = new ArrayList<DomainItemPurchase>();
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder addItem(DomainItem item, int stock) {
            itemPurchases.add(new DomainItemPurchase(item, stock));
            return this;
        }

        public DomainOrder build() {
            DomainOrder order = new DomainOrder(this.itemPurchases);
            order.setId(this.id);

            return order;
        }
    }
}