package com.craftsmanship.tfm.dal.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.OrderRepository;
import com.craftsmanship.tfm.exceptions.ItemDoesNotExist;
import com.craftsmanship.tfm.exceptions.OrderDoesNotExist;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.persistence.OrderPersistence;

@Component
public class OrderDAO implements OrderPersistence{

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAO.class);

    @Autowired
    private OrderRepository orderRepository;

    private ItemDAO itemDAO;

    public OrderDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public Order create(Order order) throws ItemDoesNotExist{
        checkItemsExists(order);
        
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
        return orderRepository.save((EntityOrder) order);
    }

    @Override
    public Order delete(Long id) throws OrderDoesNotExist {
        if (!orderRepository.findById(id).isPresent()) {
            throw new OrderDoesNotExist(id);
        }
        Order deletedOrder = orderRepository.findById(id).get();
        orderRepository.delete((EntityOrder) deletedOrder);
        return deletedOrder;
    }

    private void checkItemsExists(Order order) throws ItemDoesNotExist {
        for (ItemPurchase itemPurchase : order.getItemPurchases()) {
            LOGGER.info("Checking if Item exists: " + itemPurchase.getItem());
            itemDAO.get(itemPurchase.getItem().getId());
        }
    }

}
