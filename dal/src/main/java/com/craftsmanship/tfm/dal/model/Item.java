package com.craftsmanship.tfm.dal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Item {

	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
		
	private String description;
	
	public Item() {

	}

	public Item(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	@Override
	public String toString() {
		return "Item [description=" + description  + "]";
	}
}
