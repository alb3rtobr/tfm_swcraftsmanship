package com.craftsmanship.tfm.dal.grpc.server;

import com.craftsmanship.tfm.dal.model.EntityItem;
import com.craftsmanship.tfm.dal.model.EntityOrder;
import com.craftsmanship.tfm.dal.model.OrderItem;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder.Builder;

public class EntityConversion {

    public EntityItem getItemFromGrpcItem(GrpcItem grpcItem) {
        return new EntityItem.Builder().withId(grpcItem.getId()).withName(grpcItem.getName()).withPrice(grpcItem.getPrice())
                .withStock(grpcItem.getStock()).build();
    }

    public GrpcItem getGrpcItemFromItem(EntityItem item) {
        return GrpcItem.newBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }

    public EntityOrder getOrderFromGrpcOrder(GrpcOrder grpcOrder) {
        EntityOrder order = new EntityOrder.Builder().withId(grpcOrder.getId()).build();
        for (GrpcItemPurchase purchase : grpcOrder.getListOfItemPurchasesList()) {
            
            order.add(getItemPurchaseFromGrpcItemPurchase(purchase, order));
        }

        return order;
    }

    public GrpcOrder getGrpcOrderFromOrder(EntityOrder order) {
        Builder grpcOrderBuilder = GrpcOrder.newBuilder();

        int i = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            GrpcItemPurchase grpcItemPurchase = 
                    getGrpcItemPurchaseFromItemPurchase(orderItem);
            grpcOrderBuilder.addListOfItemPurchases(i, grpcItemPurchase);
            i++;
        }

        return grpcOrderBuilder.setId(order.getId()).build();
    }

    public OrderItem getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase, EntityOrder order) {
        EntityItem item = (EntityItem) getItemFromGrpcItem(grpcPurchase.getItem());
        return new OrderItem(order, item, grpcPurchase.getQuantity());
    }

    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(OrderItem itemPurchase) {
        GrpcItem grpcItem = getGrpcItemFromItem(itemPurchase.getItem());
        return GrpcItemPurchase.newBuilder().setItem(grpcItem).setQuantity(itemPurchase.getQuantity()).build();
    }

}
