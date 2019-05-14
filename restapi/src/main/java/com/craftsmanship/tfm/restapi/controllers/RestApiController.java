package com.craftsmanship.tfm.restapi.controllers;

import java.util.List;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.OperationType;
import com.craftsmanship.tfm.restapi.kafka.service.ItemOperationService;
import com.craftsmanship.tfm.persistence.ItemsPersistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/items", method = RequestMethod.POST)
    public Item create(@RequestBody Item item) {
        LOGGER.debug("Creating item");

        // Create item
        itemsPersistence.create(item);

        // Send message to Kafka topic
        itemOperationService.sendItemOperation(new ItemOperation(OperationType.CREATED, item));
        return item;
    }

    @RequestMapping(value = "/items", method = RequestMethod.GET)
    public List<Item> list() {
        LOGGER.debug("List items");
        return itemsPersistence.list();
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.GET)
    public Item get(@PathVariable Long id) {
        LOGGER.debug("Get item with id: " + id);
        return itemsPersistence.get(id);
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.PUT)
    public Item edit(@PathVariable Long id, @RequestBody Item item) {
        LOGGER.debug("Edit item with id: " + id);
        return itemsPersistence.update(id, item);
    }

    @RequestMapping(value = "/items/{id}", method = RequestMethod.DELETE)
    public Item delete(@PathVariable Long id) {
        LOGGER.debug("Delete item with id: " + id);

        // Send message to Kafka topic
        Item item = itemsPersistence.get(id);
        itemOperationService.sendItemOperation(new ItemOperation(OperationType.DELETED, item));

        return itemsPersistence.delete(id);
    }
}