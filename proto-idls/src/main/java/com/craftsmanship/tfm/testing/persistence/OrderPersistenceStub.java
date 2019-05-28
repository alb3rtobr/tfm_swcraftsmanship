package com.craftsmanship.tfm.testing.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.DomainOrder;
import com.craftsmanship.tfm.persistence.OrderPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderPersistenceStub implements OrderPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPersistenceStub.class);

    private ItemPersistenceStub itemPersistenceStub;
    private Map<Long, DomainOrder> orders = new HashMap<Long, DomainOrder>();
    private Long currentIndex = 1L;

    public OrderPersistenceStub(ItemPersistenceStub itemPersistenceStub) {
        this.itemPersistenceStub = itemPersistenceStub;
    }

    @Override
    public DomainOrder create(DomainOrder order) throws ItemDoesNotExist {
        checkItemsExists(order);

        // TODO: Order should be a COPY

        order.setId(currentIndex);
        orders.put(currentIndex, order);
        currentIndex++;
        return order;
    }

    @Override
    public List<DomainOrder> list() {
        return new ArrayList<DomainOrder>(orders.values());
    }

    @Override
    public DomainOrder get(Long id) throws OrderDoesNotExist {
        if (orders.get(id) == null) {
            throw new OrderDoesNotExist(id);
        }
        return orders.get(id);
    }

    @Override
    public DomainOrder update(Long id, DomainOrder order) throws OrderDoesNotExist, ItemDoesNotExist {
        if (orders.get(id) == null) {
            throw new OrderDoesNotExist(id);
        }

        checkItemsExists(order);

        order.setId(id);
        orders.put(id, order);
        return order;
    }

    @Override
    public DomainOrder delete(Long id) throws OrderDoesNotExist {
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

    public ItemPersistenceStub getItemPersistenceStub() {
        return this.itemPersistenceStub;
    }

    private void checkItemsExists(DomainOrder order) throws ItemDoesNotExist {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            LOGGER.info("Checking if Item exists: " + itemPurchase.getItem());
            itemPersistenceStub.get(itemPurchase.getItem().getId());
        }
    }
}