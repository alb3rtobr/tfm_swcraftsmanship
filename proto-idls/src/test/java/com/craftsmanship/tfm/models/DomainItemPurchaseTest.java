package com.craftsmanship.tfm.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class DomainItemPurchaseTest {

    @Test
    public void test_when_two_equals_item_purchases_when_compared_then_return_true() {
        DomainItem item1 = new DomainItem.Builder().withName("Shoe").withPrice(2).withStock(100).build();
        DomainItem item2 = new DomainItem.Builder().withName("Shoe").withPrice(2).withStock(100).build();

        DomainItemPurchase itemPurchase1 = new DomainItemPurchase(item1, 10);
        DomainItemPurchase itemPurchase2 = new DomainItemPurchase(item2, 10);

        assertThat(itemPurchase1, equalTo(itemPurchase2));
    }

    @Test
    public void test_when_two_item_purchases_with_different_item_when_compared_then_return_false() {
        DomainItem item1 = new DomainItem.Builder().withName("Shoe").withPrice(2).withStock(100).build();
        DomainItem item2 = new DomainItem.Builder().withName("Car").withPrice(2).withStock(100).build();

        DomainItemPurchase itemPurchase1 = new DomainItemPurchase(item1, 10);
        DomainItemPurchase itemPurchase2 = new DomainItemPurchase(item2, 10);

        assertThat(itemPurchase1, not(equalTo(itemPurchase2)));
    }

    @Test
    public void test_when_two_item_purchases_with_different_quantity_when_compared_then_return_false() {
        DomainItem item1 = new DomainItem.Builder().withName("Shoe").withPrice(2).withStock(100).build();
        DomainItem item2 = new DomainItem.Builder().withName("Shoe").withPrice(2).withStock(100).build();

        DomainItemPurchase itemPurchase1 = new DomainItemPurchase(item1, 10);
        DomainItemPurchase itemPurchase2 = new DomainItemPurchase(item2, 50);

        assertThat(itemPurchase1, not(equalTo(itemPurchase2)));
    }
}
