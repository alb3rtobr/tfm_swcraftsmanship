package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.models.DomainItem;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;

public interface ConversionLogic {

    DomainItem getItemFromGrpcItem(GrpcItem grpcItem);

    GrpcItem getGrpcItemFromItem(DomainItem item);

    ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase);

    GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase itemPurchase);

    Order getOrderFromGrpcOrder(GrpcOrder grpcOrder);

    GrpcOrder getGrpcOrderFromOrder(Order order);

}