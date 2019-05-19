package com.craftsmanship.tfm.stockchecker.kafka;

import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.persistence.ItemPersistence;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

public class KafkaConsumer {

	private final String TOPIC_NAME = "mytopic";

	@Autowired
	private ItemPersistence itemsPersistence;

	@Autowired
	private RestClient restClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	// TODO: Used in tests with Mockito
	public ItemPersistence getItemsPersistence() {
		return this.itemsPersistence;
	}

	// TODO: Used in tests with Mockito
	public void setRestClient(RestClient restClient) {
		this.restClient = restClient;
	}

	// TODO: UGLY!!!
	public void resetLatch(int i) {
		latch = new CountDownLatch(i);
	}

	@KafkaListener(topics = TOPIC_NAME)
	public void consume(ItemOperation payload) throws CustomException {
		LOGGER.info("received payload='{}'", payload.toString());
		restClient.sendPurchaseOrder(payload.getItem(),itemsPersistence.count());
		latch.countDown();				
	}
}
