
package com.craftsmanship.tfm.stockchecker.rest;

import com.craftsmanship.tfm.models.Item;

public interface RestClient {

	public void sendPurchaseOrder(Item item);
	
}
