package com.craftsmanship.tfm.stockchecker.kafka;

import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.stockchecker.grpc.ItemsPersistence;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

public class KafkaConsumer {

	@Value("${kafka.topic.json}")
	public String TOPIC_NAME="mytopic";
	
	@Autowired
	private ItemsPersistence itemsPersistence;

	@Autowired
	private RestClient restClient;
	
	@Value("${stockchecker.threshold}")
	private int MIN_STOCK_THRESHOLD;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	//TODO: Used in tests with Mockito
	public ItemsPersistence getItemsPersistence() {
		return this.itemsPersistence;
	}
	
	//TODO: Used in tests with Mockito
	public void setRestClient(RestClient restClient) {
		this.restClient=restClient;
	}
	
	//TODO: UGLY!!!
	public void resetLatch(int i) {
		latch = new CountDownLatch(i);
	}

	@KafkaListener(topics = "${kafka.topic.json}")
	public void consume(ItemOperation payload) {
		LOGGER.info("received payload='{}'", payload.toString());
		if (itemsPersistence.count()<MIN_STOCK_THRESHOLD) {
			LOGGER.info("Items below threshold ( "+itemsPersistence.count()+"<"+MIN_STOCK_THRESHOLD+" ), contacting REST API.");
			restClient.sendPurchaseOrder(payload.getItem());
			latch.countDown();
		}else {
			LOGGER.info("Items above threshold ( "+itemsPersistence.count()+">="+MIN_STOCK_THRESHOLD+" ), NOT contacting REST API.");
		}		
	}
}
