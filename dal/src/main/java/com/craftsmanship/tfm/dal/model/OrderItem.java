package com.craftsmanship.tfm.dal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Transactional
@Entity
@Table(name = "order_item")
public class OrderItem {

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

    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return "OrderItem [id=" + this.id + ", quantity=" + this.quantity + 
                ", item=" + this.item + ", entityOrder=" + this.entityOrder
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityOrder == null) ? 0 : entityOrder.getId().hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((item == null) ? 0 : item.getName().hashCode());
        result = prime * result + quantity;
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
        OrderItem other = (OrderItem) obj;
        if (entityOrder == null) {
            if (other.entityOrder != null)
                return false;
        } else if (!entityOrder.equals(other.entityOrder))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (item == null) {
            if (other.item != null)
                return false;
        } else if (!item.equals(other.item))
            return false;
        if (quantity != other.quantity)
            return false;
        return true;
    }

}
