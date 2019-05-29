package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.ItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderItemRepository;
import com.craftsmanship.tfm.dal.repository.OrderRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.persistence.OrderPersistence;

@Component
public class OrderDAO implements OrderPersistence {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAO.class);

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order create(Order order) throws ItemDoesNotExist {
        LOGGER.info("Check if items exist");
        checkItemsExists(order);
        LOGGER.info("All the items exist");
        saveOrderItems(order);
        LOGGER.info("All order items saved");
        return orderRepository.save((EntityOrder) order);
    }

    @Override
    public List<Order> list() {
        return new ArrayList<Order>(orderRepository.findAll());
    }

    @Override
    public Order get(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        return orderRepository.findById(id).get();
    }

    @Override
    public Order update(Long id, Order order) throws OrderDoesNotExist, ItemDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        checkItemsExists(order);
        saveOrderItems(order);
        return orderRepository.save((EntityOrder) order);
    }

    @Override
    public Order delete(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        Order deletedOrder = orderRepository.findById(id).get();
        orderRepository.delete((EntityOrder) deletedOrder);
        deleteOrderItems(deletedOrder);
        return deletedOrder;
    }

    private void checkItemsExists(Order order) throws ItemDoesNotExist {
        LOGGER.info("Checking ItemPurchases: " + order.getItemPurchases());
        try {
            for (ItemPurchase itemPurchase : order.getItemPurchases()) {
                LOGGER.info("Checking ItemPurchase: " + itemPurchase);
                LOGGER.info("Checking if Item exists: " + itemPurchase.getItem());
                itemRepository.getOne(itemPurchase.getItem().getId());
            }
        } catch (EntityNotFoundException e) {
            throw new ItemDoesNotExist(e.getMessage());
        }
    }
    
    private void saveOrderItems(Order order) {
        LOGGER.info("Saving order items: " + order.getItemPurchases());
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            orderItemRepository.save((OrderItem) itemPurchase);
        }
    }

    private void deleteOrderItems(Order order) {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            orderItemRepository.save((OrderItem) itemPurchase);
        }
    }
    
    private void saveOrderItems(Order order) {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            orderItemRepository.save((OrderItem) itemPurchase);
        }
    }

    private void deleteOrderItems(Order order) {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            orderItemRepository.save((OrderItem) itemPurchase);
        }
    }

}
