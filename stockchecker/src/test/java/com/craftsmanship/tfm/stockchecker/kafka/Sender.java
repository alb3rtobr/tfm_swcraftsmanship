package com.craftsmanship.tfm.stockchecker.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;

public class Sender {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Sender.class);

  @Autowired
  private KafkaTemplate<String, ItemOperation> kafkaTemplate;

  public void send(ItemOperation payload) {
    LOGGER.info("sending payload='{}'", payload);
    kafkaTemplate.send(KafkaConsumerTest.RECEIVER_TOPIC, payload);
  }
}
