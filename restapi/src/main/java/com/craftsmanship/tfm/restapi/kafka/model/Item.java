package com.craftsmanship.tfm.restapi.kafka.model;

import java.util.Objects;

public class Item {
    private int id;
    private String description;

    private Item() {
    }

    private Item(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Item)) {
            return false;
        }
        Item item = (Item) o;
        return id == item.id && Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + this.id + "'" +
            ", description='" + this.description + "'" +
            "}";
    }

    public static class Builder {

        private int id;
        private String description;

        public Builder() {
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Item build() {
            Item item = new Item();
            item.id = this.id;
            item.description = this.description;

            return item;
        }
    }
}