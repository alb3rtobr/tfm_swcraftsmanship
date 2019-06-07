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

    public EntityOrder create(EntityOrder order) throws ItemDoesNotExist, ItemWithNoStockAvailable {
        checkItemsExists(order);
        checkItemsStocks(order);
        
        EntityOrder newOrder = orderRepository.saveAndFlush(new EntityOrder());
        saveOrderItems(order, newOrder);
        LOGGER.info("All order items saved");

        decreaseStocks(order);

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
        updateOrderItems(id, order);
        return orderRepository.save(order);
    }

    public EntityOrder delete(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        EntityOrder deletedOrder = orderRepository.findById(id).get();
        deleteOrderItems(deletedOrder);
        orderRepository.delete(deletedOrder);
        return deletedOrder;
    }

    private void checkItemsExists(EntityOrder order) throws ItemDoesNotExist {
        LOGGER.info("Check items exist");
        for (OrderItem itemPurchase : order.getOrderItems()) {
            if (!itemRepository.existsById(itemPurchase.getItem().getId())) {
                throw new ItemDoesNotExist("Item does not exist");
            }
        }
        LOGGER.info("All the items exist");
    }

    private void checkItemsStocks(EntityOrder order) throws ItemWithNoStockAvailable {
        LOGGER.info("Checking stocks");
        for (OrderItem itemPurchase : order.getOrderItems()) {
            EntityItem item = itemPurchase.getItem();
            if (itemPurchase.getQuantity() > item.getStock()) {
                throw new ItemWithNoStockAvailable(item, itemPurchase.getQuantity());
            }
        }
        LOGGER.info("Stocks are ok");
    }

    private void decreaseStocks(EntityOrder order) {
        LOGGER.info("Decreasing stocks");
        for (OrderItem itemPurchase : order.getOrderItems()) {
            EntityItem item = itemPurchase.getItem();
            decreaseItemStock(item, itemPurchase.getQuantity());
        }
        LOGGER.info("Decreasing stocks was ok");
    }

    private void decreaseItemStock(EntityItem item, int quantity) {
        EntityItem persistedItem = itemRepository.findById(item.getId()).get();
        item.setStock(persistedItem.getStock() - quantity);
        itemRepository.save(item);
    }

    private void increaseItemStock(EntityItem item, int quantity) {
        EntityItem persistedItem = itemRepository.findById(item.getId()).get();
        item.setStock(persistedItem.getStock() + quantity);
        itemRepository.save(item);
    }

    private void saveOrderItems(EntityOrder order, EntityOrder createdOrder) {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            itemPurchase.setOrder(createdOrder);
            createdOrder.add(orderItemRepository.save(itemPurchase));
        }
    }

    private void updateOrderItems(Long id, EntityOrder order) {
        EntityOrder orderToUpdate = orderRepository.findById(id).get();
        for (OrderItem orderItemToDelete : orderToUpdate.getOrderItems()) {
            increaseItemStock(orderItemToDelete.getItem(), orderItemToDelete.getQuantity());
            orderItemRepository.delete(orderItemToDelete);
        }
        orderToUpdate.setOrderItems(new HashSet<OrderItem>());
        
        for (OrderItem orderItemToAdd : order.getOrderItems()) {
            orderItemToAdd.setOrder(orderToUpdate);
            orderToUpdate.add(orderItemRepository.save(orderItemToAdd));
            decreaseItemStock(orderItemToAdd.getItem(), orderItemToAdd.getQuantity());
        }
    }

    private void deleteOrderItems(EntityOrder order) {
        for (OrderItem itemPurchase : order.getOrderItems()) {
            increaseItemStock(itemPurchase.getItem(), itemPurchase.getQuantity());
            orderItemRepository.delete(itemPurchase);
        }
    }

}
