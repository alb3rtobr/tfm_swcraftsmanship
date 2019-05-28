package com.craftsmanship.tfm.dal.repository;

import com.craftsmanship.tfm.dal.model.EntityOrder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<EntityOrder, Long> {

}

