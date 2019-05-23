package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.persistence.OrderPersistence;

public class OrderPersistenceStub implements OrderPersistence {
    private ItemPersistence itemPersistence;
    private Map<Long, Order> orders = new HashMap<Long, Order>();
    private Long currentIndex = 1L;

    public OrderPersistenceStub(ItemPersistence itemPersistence) {
        this.itemPersistence = itemPersistence;
    }

    public Order create(Order order) {
        order.setId(currentIndex);
        orders.put(currentIndex, order);
        currentIndex++;
        return order;
    }

    public List<Order> list() {
        return new ArrayList<Order>(orders.values());
    }

    public Order get(Long id) {
        return orders.get(id);
    }

    public Order update(Long id, Order order) {
        if (orders.get(id) == null) {
            return null;
        }

        order.setId(id);
        orders.put(id, order);
        return order;
    }

    public Order delete(Long id) {
        return orders.remove(id);
    }

    public int count() {
        return orders.size();
    }

    public void initialize() {
        orders.clear();
        currentIndex = 1L;
    }
}