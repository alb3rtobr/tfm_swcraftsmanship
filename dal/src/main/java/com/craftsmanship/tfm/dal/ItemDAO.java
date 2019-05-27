package com.craftsmanship.tfm.dal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.craftsmanship.tfm.dal.model.EntityItem;
import com.craftsmanship.tfm.dal.repository.ItemRepository;

@Component
public class ItemDAO {
	
	@Autowired
	private ItemRepository itemRepository;
	
	public EntityItem create(EntityItem item) {
		return itemRepository.save(item);
	}

	public EntityItem read(long id) {
		return itemRepository.getOne(id);
	}

	public List<EntityItem> list() {
		return itemRepository.findAll();
	}

	public EntityItem get(long id) {
		return itemRepository.findById(id).get();
	}

	public EntityItem update(long id, EntityItem item) {
		return itemRepository.save(item);
	}

	public EntityItem delete(long id) {
		EntityItem deletedItem = itemRepository.findById(id).get();
		itemRepository.delete(deletedItem);
		return deletedItem;
	}

	public int count() {
		return (int) itemRepository.count();
	}
}
