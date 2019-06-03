package com.craftsmanship.tfm.models;

import java.util.Objects;

public class Item {
    private Long id;
    private String name;
    private Long price;
    private int stock;

    private Item() {
        this(null, 0L, 0);
    }

    private Item(String name, Long price, int stock) {
        this.id = -1L;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Item(Item another) {
        this.id = another.id;
        this.name = another.name;
        this.price = another.price;
        this.stock = another.stock;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return this.price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(price, item.price)
                && Objects.equals(stock, item.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "{" + " id='" + this.id + "'" + ", name='" + this.name + "'" + ", price='" + this.price + "'"
                + ", stock='" + this.stock + "'" + "}";
    }

    public static class Builder {

        private Long id;
        private String name;
        private Long price;
        private int stock;

        public Builder() {
            this.id = -1L;
            this.name = null;
            this.price = 0L;
            this.stock = 0;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPrice(Long price) {
            this.price = price;
            return this;
        }

        public Builder withPrice(Integer price) {
            this.price = new Long(price);
            return this;
        }

        public Builder withStock(int stock) {
            this.stock = stock;
            return this;
        }

        public Item build() {
            Item item = new Item();
            item.id = this.id;
            item.name = this.name;
            item.price = this.price.longValue();
            item.stock = this.stock;

            return item;
        }
    }
}