package com.craftsmanship.tfm.models;

import java.util.Objects;

public class Item {
    private Long id;
    private String name;
    private Long price;
    private Long quantity;

    private Item() {
    }

    private Item(String name, Long price, Long quantity) {
        this.id = -1L;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Item(Item another) {
        this.id = another.id;
        this.name = another.name;
        this.price = another.price.longValue();
        this.quantity = another.quantity.longValue();
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

    public Long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(price, item.price)
                && Objects.equals(quantity, item.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "{" + " id='" + this.id + "'" + ", name='" + this.name + "'" + ", price='" + this.price + "'"
                + ", quantity='" + this.quantity + "'" + "}";
    }

    public static class Builder {

        private Long id;
        private String name;
        private Long price;
        private Long quantity;

        public Builder() {
            this.id = -1L;
            this.name = null;
            this.price = 0L;
            this.quantity = 0L;
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

        public Builder withQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withQuantity(Integer quantity) {
            this.quantity = new Long(quantity);
            return this;
        }

        public Item build() {
            Item item = new Item();
            item.id = this.id;
            item.name = this.name;
            item.price = this.price;
            item.quantity = this.quantity;

            return item;
        }
    }
}