package com.craftsmanship.tfm.restapi.kafka.service;

import com.craftsmanship.tfm.models.ItemOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class ItemOperationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemOperationService.class);

    private static final String ITEM_MODIFIED_TOPIC = "item_modified";

    @Autowired
    private KafkaTemplate<String, ItemOperation> kafkaTemplate;

    public void sendItemOperation(ItemOperation operation) {

        LOGGER.info("Sending Item Operation " + operation + " to topic " + ITEM_MODIFIED_TOPIC);
             
        ListenableFuture<SendResult<String, ItemOperation>> future = kafkaTemplate.send(ITEM_MODIFIED_TOPIC, operation);
         
        future.addCallback(new ListenableFutureCallback<SendResult<String, ItemOperation>>() {
     
            @Override
            public void onSuccess(SendResult<String, ItemOperation> result) {
                LOGGER.info("Sent operation=[" + operation + 
                  "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("Unable to send operation=["
                  + operation + "] due to : " + ex.getMessage());
            }
        });
    }
}