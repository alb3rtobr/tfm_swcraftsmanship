
package com.craftsmanship.tfm.stockchecker.rest;

import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.DomainItem;

public interface RestClient {

	public PurchaseOrder sendPurchaseOrder(DomainItem item, int currentStock);

	public RestTemplate getRestTemplate();
}
