package com.craftsmanship.tfm.persistence;

import java.io.IOException;

import com.craftsmanship.tfm.exceptions.CustomException;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceBlockingStub;
import com.craftsmanship.tfm.idls.v2.OrderPersistenceServiceGrpc.OrderPersistenceServiceStub;
import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemPurchase;
import com.craftsmanship.tfm.models.Order;
import com.craftsmanship.tfm.testing.grpc.OrderPersistenceInProcessServer;
import com.craftsmanship.tfm.testing.persistence.OrderPersistenceStub;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;

public class OrderPersistenceGrpcTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPersistenceGrpcTest.class);
}
