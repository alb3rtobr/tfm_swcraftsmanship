package com.craftsmanship.tfm.dal.grpc.server;

import com.craftsmanship.tfm.dal.model.EntityItem;
import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder.Builder;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.utils.ConversionLogic;

public class EntityConversion implements ConversionLogic{

    @Override
    public EntityItem getItemFromGrpcItem(GrpcItem grpcItem) {
        return new EntityItem.Builder().withId(grpcItem.getId()).withName(grpcItem.getName()).withPrice(grpcItem.getPrice())
                .withStock(grpcItem.getStock()).build();
    }

    @Override
    public GrpcItem getGrpcItemFromItem(Item item) {
        return GrpcItem.newBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice())
                .setStock(item.getStock()).build();
    }

    @Override
    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase arg0) {
        // TODO THIS FUNCIONALITY IS NOT READY YET....
        return null;
    }

    @Override
    public GrpcOrder getGrpcOrderFromOrder(Order arg0) {
        // TODO THIS FUNCIONALITY IS NOT READY YET....
        return null;
    }

    @Override
    public ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase arg0) {
        // TODO THIS FUNCIONALITY IS NOT READY YET....
        return null;
    }

    @Override
    public Order getOrderFromGrpcOrder(GrpcOrder arg0) {
        // TODO THIS FUNCIONALITY IS NOT READY YET....
        return null;
    }

    // TODO: THIS FUNCIONALITY IS NOT READY YET....
//    @Override
//    public ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase) {
//        EntityItem item = getItemFromGrpcItem(grpcPurchase.getItem());
//        return new ItemPurchase(item, grpcPurchase.getQuantity());
//    }
//
//    @Override
//    public GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase itemPurchase) {
//        GrpcItem grpcItem = getGrpcItemFromItem(itemPurchase.getItem());
//        return GrpcItemPurchase.newBuilder().setItem(grpcItem).setQuantity(itemPurchase.getQuantity()).build();
//    }
//
//    @Override
//    public Order getOrderFromGrpcOrder(GrpcOrder grpcOrder) {
//        Order order = new Order.Builder().withId(grpcOrder.getId()).build();
//        for (GrpcItemPurchase purchase : grpcOrder.getListOfItemPurchasesList()) {
//            order.add(getItemPurchaseFromGrpcItemPurchase(purchase));
//        }
//
//        return order;
//    }
//
//    @Override
//    public GrpcOrder getGrpcOrderFromOrder(Order order) {
//        Builder grpcOrderBuilder = GrpcOrder.newBuilder();
//        for (int i = 0; i < order.getItemPurchases().size(); i++) {
//            GrpcItemPurchase grpcItemPurchase = 
//                    getGrpcItemPurchaseFromItemPurchase(order.getItemPurchases().get(i));
//            grpcOrderBuilder.addListOfItemPurchases(i, grpcItemPurchase);
//        }
//        return grpcOrderBuilder.setId(order.getId()).build();
//    }

}
