package com.craftsmanship.tfm.stockchecker.rest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.stockchecker.rest.PurchaseOrder;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("dev")
public class PurchaseOrderClientTest {

	@Autowired
	private RestClient client;

	private final static String RECEIVER_TOPIC = "mytopic";

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, RECEIVER_TOPIC);

	@BeforeClass
	public static void setup() {
		System.setProperty("kafka.bootstrap-servers", embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Test
	public void whenItemsAreBelowThreshold_thenRestApiIsCalledWithExpectedItem() throws Exception {

		Item item = new Item.Builder().withDescription("PlayStation4").build();
		PurchaseOrder nullOrder = client.sendPurchaseOrder(item, 3);
		assertNull(nullOrder);
	}

	@Test
	public void whenItemsAreAboveThreshold_thenRestApiIsNotCalled() {
		Item item = new Item.Builder().withDescription("MegaDrive").build();
		PurchaseOrder order = client.sendPurchaseOrder(item, 0);
		PurchaseOrder expected = new PurchaseOrder(item);
		assertNotNull(order);
		assertEquals("PurchaseOrder generated is not the expected.", order.getItem().getDescription(),
				expected.getItem().getDescription());
	}
}
