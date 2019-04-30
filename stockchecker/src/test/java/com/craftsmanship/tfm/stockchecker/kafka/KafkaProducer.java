package com.craftsmanship.tfm.stockchecker.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;

public class KafkaProducer {

	@Value("${kafka.topic.json}")
	private static String TOPIC_NAME="mytopic";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

	  @Autowired
	  private KafkaTemplate<String, ItemOperation> kafkaTemplate;

	  public void send(ItemOperation itemOp) {
	    LOGGER.info("sending itemOp='{}'", itemOp.toString());
	    kafkaTemplate.send(TOPIC_NAME, itemOp);
	  }
}
