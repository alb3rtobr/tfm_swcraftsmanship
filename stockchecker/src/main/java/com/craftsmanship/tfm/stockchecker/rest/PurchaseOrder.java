package com.craftsmanship.tfm.stockchecker.rest;

import com.craftsmanship.tfm.models.DomainItem;

public class PurchaseOrder {

	private DomainItem item;
	
	public PurchaseOrder(DomainItem item) {
		this.item=item;
	}
	
	public DomainItem getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		String result="Purchase item ["+this.item.toString()+"]";
		return result;
	}
		
}
