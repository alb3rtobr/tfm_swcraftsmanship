package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.ItemPurchase;
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

    @Override
    public Order create(Order order) throws ItemDoesNotExist {
        checkItemsExists(order);

        // TODO: Order should be a COPY

        order.setId(currentIndex);
        orders.put(currentIndex, order);
        currentIndex++;
        return order;
    }

    @Override
    public List<Order> list() {
        return new ArrayList<Order>(orders.values());
    }

    @Override
    public Order get(Long id) throws OrderDoesNotExist {
        if (orders.get(id) == null) {
            throw new OrderDoesNotExist(id);
        }
        return orders.get(id);
    }

    @Override
    public Order update(Long id, Order order) throws OrderDoesNotExist, ItemDoesNotExist {
        if (orders.get(id) == null) {
            throw new OrderDoesNotExist(id);
        }

        checkItemsExists(order);

        order.setId(id);
        orders.put(id, order);
        return order;
    }

    @Override
    public Order delete(Long id) throws OrderDoesNotExist {
        if (orders.get(id) == null) {
            throw new OrderDoesNotExist(id);
        }

        return orders.remove(id);
    }

    public int count() {
        return orders.size();
    }

    public void initialize() {
        orders.clear();
        currentIndex = 1L;
    }

    private void checkItemsExists(Order order) throws ItemDoesNotExist {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            itemPersistence.get(itemPurchase.getItem().getId());
        }
    }
}