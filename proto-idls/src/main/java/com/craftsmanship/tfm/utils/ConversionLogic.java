package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.models.DomainItemPurchase;

public interface ConversionLogic {

    Item getItemFromGrpcItem(GrpcItem grpcItem);

    GrpcItem getGrpcItemFromItem(Item item);

    Order getOrderFromGrpcOrder(GrpcOrder grpcOrder);

    GrpcOrder getGrpcOrderFromOrder(Order order);
}