package com.craftsmanship.tfm.dal.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class EntityItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private long price;
    private int stock;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "EntityItem [id=" + this.id + ", name=" + this.name + ", price=" + this.price + 
                ", stock=" + this.stock + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((orderItems == null) ? 0 : orderItems.hashCode());
        result = prime * result + (int) (price ^ (price >>> 32));
        result = prime * result + stock;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityItem other = (EntityItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (orderItems == null) {
            if (other.orderItems != null)
                return false;
        } else if (!orderItems.equals(other.orderItems))
            return false;
        if (price != other.price)
            return false;
        if (stock != other.stock)
            return false;
        return true;
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
