package com.craftsmanship.tfm.dal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.craftsmanship.tfm.dal.model.Item;
import com.craftsmanship.tfm.dal.repository.ItemRepository;

public class DataAccess {
	
	@Autowired
	private ItemRepository itemRepository;
	
	public Item create(Item item) {
		return itemRepository.save(item);
	}

	public Item read(long id) {
		return itemRepository.getOne(id);
	}

	public List<Item> list() {
		return itemRepository.findAll();
	}

	public Item get(long id) {
		return itemRepository.findById(id).get();
	}

	public Item update(long id, Item item) {
		return itemRepository.save(item);
	}

	public Item delete(long id) {
		Item deletedItem = itemRepository.findById(id).get();
		itemRepository.delete(deletedItem);
		return deletedItem;
	}

	public int count() {
		return (int) itemRepository.count();
	}
}
