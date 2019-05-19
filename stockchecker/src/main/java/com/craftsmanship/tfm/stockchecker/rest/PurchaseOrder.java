package com.craftsmanship.tfm.stockchecker.rest;

import com.craftsmanship.tfm.models.Item;

public class PurchaseOrder {

	private Item item;
	
	public PurchaseOrder(Item item) {
		this.item=item;
	}
	
	public Item getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		String result="Purchase item ["+this.item.toString()+"]";
		return result;
	}
		
}
