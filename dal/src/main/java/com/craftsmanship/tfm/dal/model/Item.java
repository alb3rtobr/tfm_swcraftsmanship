package com.craftsmanship.tfm.dal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
		
	private String name;
	
	protected Item() {

	}

	public Item(String name) {
		this.name = name;
	}
	
	public Item(long id, String name) {
		this(name);
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Item [name=" + name  + "]";
	}
}
