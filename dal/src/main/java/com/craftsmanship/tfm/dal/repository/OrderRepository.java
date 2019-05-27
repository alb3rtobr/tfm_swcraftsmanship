package com.craftsmanship.tfm.dal.repository;

import com.craftsmanship.tfm.dal.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}

