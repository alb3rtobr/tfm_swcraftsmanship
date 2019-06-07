package com.craftsmanship.tfm.stockchecker.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.craftsmanship.tfm.models.Item;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class PurchaseOrderClient implements RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderClient.class);

    private final String uri;

    private int stockThreshold;

    private final Counter itemOutOfStockCounter;

    public PurchaseOrderClient(String restHost, int restPort, String restEndPoint, int stockThreshold, MeterRegistry meterRegistry) {

        this.uri = "http://"+restHost+":"+restPort+"/"+restEndPoint;
        this.restTemplate = new RestTemplate();
        this.stockThreshold=stockThreshold;
        
        this.itemOutOfStockCounter = Counter
                .builder("item_out_of_stock")
                .description("Number of purchases sent due to item out of stock")
                .register(meterRegistry);
    }

    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

    @Override
    public PurchaseOrder sendPurchaseOrder(Item item, int currentStock) {
        if (currentStock<this.stockThreshold) {
            PurchaseOrder newOrder = new PurchaseOrder(item);
            LOGGER.info("Items below threshold ( "+currentStock+"<"+this.stockThreshold+" ), contacting REST API.");
            //PurchaseOrder result=restTemplate.postForObject( uri, newOrder, PurchaseOrder.class);
            // By the moment, only a log is added.
            // This function could be impacted by the new model.
            LOGGER.info("sendPurchaseOrder sent order for item ["+item.toString()+"]");

            itemOutOfStockCounter.increment();
            return newOrder;

        }else {
            LOGGER.info("Items above threshold ( "+currentStock+">="+this.stockThreshold+" ), NOT contacting REST API.");
            return null;
        }		
    }

}
