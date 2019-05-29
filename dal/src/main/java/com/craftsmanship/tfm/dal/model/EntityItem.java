package com.craftsmanship.tfm.dal.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.craftsmanship.tfm.models.Item;

@Entity
@Table(name = "item")
public class EntityItem implements Item{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private long price;
    private int stock;

    @OneToMany(mappedBy = "item")
    private Set <OrderItem> orderItems;
    
    protected EntityItem() {

    }

    public EntityItem(String name, long price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public EntityItem(long id, String name, long price, int stock) {
        this(name, price, stock);
        this.id = id;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getPrice() {
        return price;
    }

    @Override
    public void setPrice(Long price) {
        this.price = price;
    }

    @Override
    public int getStock() {
        return stock;
    }

    @Override
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof EntityItem)) {
            return false;
        }
        EntityItem item = (EntityItem) o;
        return Objects.equals(id, item.id) 
                && Objects.equals(name, item.name) 
                && Objects.equals(price, item.price)
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

        public EntityItem build() {
            EntityItem item = new EntityItem();
            item.id = this.id;
            item.name = this.name;
            item.price = this.price.longValue();
            item.stock = this.stock;

            return item;
        }
    }
}
