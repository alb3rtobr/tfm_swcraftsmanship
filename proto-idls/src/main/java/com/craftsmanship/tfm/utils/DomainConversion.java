package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.DomainOrder;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder.Builder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;

public class DomainConversion implements ConversionLogic {

    @Override
    public DomainItem getItemFromGrpcItem(GrpcItem grpcItem) {
        return new DomainItem.Builder().withId(grpcItem.getId()).withName(grpcItem.getName()).withPrice(grpcItem.getPrice())
                .withStock(grpcItem.getStock()).build();
    }

    @Override
    public GrpcItem getGrpcItemFromItem(Item item) {
        return GrpcItem.newBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }

    @Override
    public ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase) {
        DomainItem item = getItemFromGrpcItem(grpcPurchase.getItem());
        return new ItemPurchase(item, grpcPurchase.getQuantity());
    }

    @Override
    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase itemPurchase) {
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
    public GrpcOrder getGrpcOrderFromOrder(DomainOrder order) {
        Builder grpcOrderBuilder = GrpcOrder.newBuilder();
        for (int i = 0; i < order.getItemPurchases().size(); i++) {
            GrpcItemPurchase grpcItemPurchase = 
                    getGrpcItemPurchaseFromItemPurchase(order.getItemPurchases().get(i));
            grpcOrderBuilder.addListOfItemPurchases(i, grpcItemPurchase);
        }
        return grpcOrderBuilder.setId(order.getId()).build();
    }
}