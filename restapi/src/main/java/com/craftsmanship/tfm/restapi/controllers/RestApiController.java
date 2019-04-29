package com.craftsmanship.tfm.restapi.controllers;

import com.craftsmanship.tfm.restapi.kafka.model.Item;
import com.craftsmanship.tfm.restapi.kafka.model.ItemOperation;
import com.craftsmanship.tfm.restapi.kafka.service.ItemOperationService;
import com.craftsmanship.tfm.restapi.persistence.ItemsPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class RestApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestApiController.class);

    // Persistence Handler
    @Autowired
    private ItemsPersistence itemsPersistence;

    // Kafka Message Bus Service
    private final ItemOperationService itemOperationService;

    public RestApiController(ItemsPersistence itemsPersistence, ItemOperationService itemOperationService) {
        this.itemsPersistence = itemsPersistence;
        this.itemOperationService = itemOperationService;
    }

    // @RequestMapping(value = "/items", method = RequestMethod.GET)
    // public List<Item> list() {
    //     LOGGER.debug("Returning items");
    //     return 
    // }

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public Item create(@RequestBody Item item) {
        LOGGER.debug("Creating item");

        // Create item
        itemsPersistence.create(item);

        // Send message to Kafka topic
        itemOperationService.sendItemOperation(new ItemOperation(item));
        return item;
    }
}