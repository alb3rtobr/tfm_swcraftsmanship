package com.craftsmanship.tfm.utils;

import com.craftsmanship.tfm.idls.v2.ItemPersistence.GrpcItem;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcItemPurchase;
import com.craftsmanship.tfm.idls.v2.OrderPersistence.GrpcOrder;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.DomainOrder;

public interface ConversionLogic {

    Item getItemFromGrpcItem(GrpcItem grpcItem);

    GrpcItem getGrpcItemFromItem(Item item);

    ItemPurchase getItemPurchaseFromGrpcItemPurchase(GrpcItemPurchase grpcPurchase);

    GrpcItemPurchase getGrpcItemPurchaseFromItemPurchase(ItemPurchase itemPurchase);

    DomainOrder getOrderFromGrpcOrder(GrpcOrder grpcOrder);

    GrpcOrder getGrpcOrderFromOrder(DomainOrder order);

}