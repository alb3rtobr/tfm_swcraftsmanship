package com.craftsmanship.tfm.dal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.craftsmanship.tfm.models.ItemPurchase;

@Entity
@Table(name = "order_item")
public class OrderItem implements ItemPurchase{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private EntityItem item;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private EntityOrder entityOrder;

    public OrderItem() {
        this(null, null, 0);
    }

    public OrderItem(EntityOrder order, EntityItem item, int quantity) {
        this.entityOrder = order;
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public EntityItem getItem() {
        return this.item;
    }

    public void setItem(EntityItem item) {
        this.item = item;
    }

    public EntityOrder getOrder() {
        return this.entityOrder;
    }

    public void setOrder(EntityOrder order) {
        this.entityOrder = order;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderItem other = (OrderItem) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "OrderItem [id=" + id + ", quantity=" + quantity + ", item=" + item + ", order=" + entityOrder + "]";
    }

}
