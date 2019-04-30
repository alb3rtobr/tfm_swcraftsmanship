package com.craftsmanship.tfm.stockchecker.kafka;

import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.craftsmanship.tfm.models.ItemOperation;

public class KafkaConsumer {

	@Value("${kafka.topic.json}")
	public static String TOPIC_NAME="mytopic";

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	@KafkaListener(topics = "${kafka.topic.json}")
	public void consume(ItemOperation payload) {
		LOGGER.info("received payload='{}'", payload.toString());
		latch.countDown();
	}
}
