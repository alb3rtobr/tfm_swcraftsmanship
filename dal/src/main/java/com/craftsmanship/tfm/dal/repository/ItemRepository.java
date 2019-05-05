package com.craftsmanship.tfm.dal.repository;

import com.craftsmanship.tfm.dal.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
	
}

