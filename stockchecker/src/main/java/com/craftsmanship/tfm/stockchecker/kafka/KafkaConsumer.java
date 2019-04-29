package com.craftsmanship.tfm.stockchecker.kafka;

import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;

public class KafkaConsumer {

	private final String TOPIC_NAME="mytopic";

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	@KafkaListener(topics = TOPIC_NAME)
	public void receive(ItemOperation payload) {
		LOGGER.info("received payload='{}'", payload.toString());
		latch.countDown();
	}
}
