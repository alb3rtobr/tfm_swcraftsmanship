package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder.Builder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;

public class ConversionLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionLogic.class);

    public Item getItemFromGrpcItem(GrpcItem grpcItem) {
        return new Item.Builder().withId(grpcItem.getId()).withName(grpcItem.getName()).withPrice(grpcItem.getPrice())
                .withStock(grpcItem.getStock()).build();
    }

    public GrpcItem getGrpcItemFromItem(Item item) {
        LOGGER.info("getGrpcItemFromItem ITEM: " + item);
        return GrpcItem.newBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }

    public Order getOrderFromGrpcOrder(GrpcOrder grpcOrder) {
        Order order = new Order.Builder().withId(grpcOrder.getId()).build();
        for (GrpcItemPurchase purchase : grpcOrder.getListOfItemPurchasesList()) {
            order.add(getItemPurchaseFromGrpcItemPurchase(purchase));
        }

        return order;
    }

    public GrpcOrder getGrpcOrderFromOrder(Order order) {
        Builder grpcOrderBuilder = GrpcOrder.newBuilder();
        for (int i = 0; i < order.getItemPurchases().size(); i++) {
            GrpcItemPurchase grpcItemPurchase = 
                    getGrpcItemPurchaseFromItemPurchase(order.getItemPurchases().get(i));
            grpcOrderBuilder.addListOfItemPurchases(i, grpcItemPurchase);
        }
        return grpcOrderBuilder.setId(order.getId()).build();
    }

    public ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase) {
        Item item = getItemFromGrpcItem(grpcPurchase.getItem());
        return new ItemPurchase(item, grpcPurchase.getQuantity());
    }

    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase itemPurchase) {
        GrpcItem grpcItem = getGrpcItemFromItem(itemPurchase.getItem());
        return GrpcItemPurchase.newBuilder().setItem(grpcItem).setQuantity(itemPurchase.getQuantity()).build();
    }


}