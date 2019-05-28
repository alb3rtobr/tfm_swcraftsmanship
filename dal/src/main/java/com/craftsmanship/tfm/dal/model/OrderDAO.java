package com.craftsmanship.tfm.dal.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.repository.OrderRepository;

@Component
public class OrderDAO {

    @Autowired
    private OrderRepository orderRepository;

    public Order create(Order order) {
        return orderRepository.save(order);
    }

    public Order read(long id) {
        return orderRepository.getOne(id);
    }

    public List<Order> list() {
        return orderRepository.findAll();
    }

    public Order get(long id) {
        return orderRepository.findById(id).get();
    }

    public Order update(long id, Order order) {
        return orderRepository.save(order);
    }

    public Order delete(long id) {
        Order deletedOrder = orderRepository.findById(id).get();
        orderRepository.delete(deletedOrder);
        return deletedOrder;
    }

    public int count() {
        return (int) orderRepository.count();
    }
}
