package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.models.DomainItemPurchase;
import com.craftsmanship.tfm.models.DomainOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder.Builder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;

public class DomainConversion implements ConversionLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainConversion.class);

    @Override
    public DomainItem getItemFromGrpcItem(GrpcItem grpcItem) {
        return new DomainItem.Builder().withId(grpcItem.getId()).withName(grpcItem.getName()).withPrice(grpcItem.getPrice())
                .withStock(grpcItem.getStock()).build();
    }

    @Override
    public GrpcItem getGrpcItemFromItem(Item item) {
        LOGGER.info("getGrpcItemFromItem ITEM: " + item);
        return GrpcItem.newBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }

    @Override
    public DomainItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase) {
        DomainItem item = getItemFromGrpcItem(grpcPurchase.getItem());
        return new DomainItemPurchase(item, grpcPurchase.getQuantity());
    }

    @Override
    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(DomainItemPurchase itemPurchase) {
        GrpcItem grpcItem = getGrpcItemFromItem(itemPurchase.getItem());
        return GrpcItemPurchase.newBuilder().setItem(grpcItem).setQuantity(itemPurchase.getQuantity()).build();
    }

    @Override
    public DomainOrder getOrderFromGrpcOrder(GrpcOrder grpcOrder) {
        DomainOrder order = new DomainOrder.Builder().withId(grpcOrder.getId()).build();
        for (GrpcItemPurchase purchase : grpcOrder.getListOfItemPurchasesList()) {
            order.add(getItemPurchaseFromGrpcItemPurchase(purchase));
        }

        return order;
    }

    @Override
    public GrpcOrder getGrpcOrderFromOrder(Order order) {
        Builder grpcOrderBuilder = GrpcOrder.newBuilder();
        for (int i = 0; i < order.getItemPurchases().size(); i++) {
            GrpcItemPurchase grpcItemPurchase = 
                    getGrpcItemPurchaseFromItemPurchase((DomainItemPurchase) order.getItemPurchases().get(i));
            grpcOrderBuilder.addListOfItemPurchases(i, grpcItemPurchase);
        }
        return grpcOrderBuilder.setId(order.getId()).build();
    }
}