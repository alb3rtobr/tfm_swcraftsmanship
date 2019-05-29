package com.craftsmanship.tfm.dal.repository;

import com.craftsmanship.tfm.dal.model.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
}

