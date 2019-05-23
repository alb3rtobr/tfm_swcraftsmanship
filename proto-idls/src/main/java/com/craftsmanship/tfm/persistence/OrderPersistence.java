package com.craftsmanship.tfm.persistence;

import java.util.List;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.models.Order;

public interface OrderPersistence {
    public Order create(Order order) throws CustomException;
    public List<Order> list() throws CustomException;
    public Order get(Long id) throws CustomException;
    public Order update(Long id, Order order) throws CustomException;
    public Order delete(Long id) throws CustomException;
}