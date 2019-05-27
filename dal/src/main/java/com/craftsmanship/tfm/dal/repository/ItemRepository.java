package com.craftsmanship.tfm.dal.repository;

import com.craftsmanship.tfm.dal.model.EntityItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<EntityItem, Long> {
	
}

