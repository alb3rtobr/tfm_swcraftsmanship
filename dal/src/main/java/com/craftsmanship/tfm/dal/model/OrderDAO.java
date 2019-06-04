package com.craftsmanship.tfm.dal.model;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;

@Component
public class OrderDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAO.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public EntityOrder create(EntityOrder order) throws ItemDoesNotExist {
        LOGGER.info("Check if items exist");
        checkItemsExists(order);
        LOGGER.info("All the items exist");
        EntityOrder newOrder = orderRepository.saveAndFlush(new EntityOrder());
        saveOrderItems(order, newOrder);
        LOGGER.info("All order items saved");
        return orderRepository.saveAndFlush(newOrder);
    }

    public Set<EntityOrder> list() {
        return new HashSet<EntityOrder>(orderRepository.findAll());
    }

    public EntityOrder get(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        return orderRepository.findById(id).get();
    }

    public EntityOrder update(Long id, EntityOrder order) throws OrderDoesNotExist, ItemDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        checkItemsExists(order);
        saveOrderItems(order, order);
        return orderRepository.save((EntityOrder) order);
    }

    public EntityOrder delete(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        EntityOrder deletedOrder = orderRepository.findById(id).get();
        orderRepository.delete((EntityOrder) deletedOrder);
        deleteOrderItems(deletedOrder);
        return deletedOrder;
    }

    private void checkItemsExists(EntityOrder order) throws ItemDoesNotExist {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            if (!itemRepository.existsById(itemPurchase.getItem().getId())) {
                throw new ItemDoesNotExist("Item does not exist");
            }
        }
    }

    private void saveOrderItems(EntityOrder order, EntityOrder createdOrder) {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            itemPurchase.setOrder(createdOrder);
            createdOrder.add(orderItemRepository.save(itemPurchase));
        }
    }

    private void deleteOrderItems(EntityOrder order) {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            orderItemRepository.save((OrderItem) itemPurchase);
        }
    }

}
