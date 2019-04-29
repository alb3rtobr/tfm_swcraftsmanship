package com.craftsmanship.tfm.stockchecker.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;

public class KafkaProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

	  @Value("${kafka.topic.json}")
	  private String jsonTopic;

	  @Autowired
	  private KafkaTemplate<String, ItemOperation> kafkaTemplate;

	  public void send(ItemOperation itemOp) {
	    LOGGER.info("sending car='{}'", itemOp.toString());
	    kafkaTemplate.send(jsonTopic, itemOp);
	  }
}
