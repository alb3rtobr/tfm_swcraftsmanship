package com.craftsmanship.tfm.models;

import java.util.Objects;

public class Item {
    private Long id;
    private String description;

    private Item() {
    }

    private Item(String description) {
        this.id = 0L;
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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

        private Long id;
        private String description;

        public Builder() {
            this.id = 0L;
        }

        public Builder withId(Long id) {
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