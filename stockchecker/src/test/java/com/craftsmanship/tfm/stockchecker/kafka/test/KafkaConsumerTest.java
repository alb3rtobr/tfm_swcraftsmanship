package com.craftsmanship.tfm.stockchecker.kafka.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.models.Item;
import com.craftsmanship.tfm.models.ItemOperation;
import com.craftsmanship.tfm.models.OperationType;

import com.craftsmanship.tfm.stockchecker.kafka.KafkaConsumer;
import com.craftsmanship.tfm.stockchecker.rest.RestClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class KafkaConsumerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTest.class);

	private final static String RECEIVER_TOPIC = "item_modified";

	@Autowired
	private KafkaConsumer consumer;

	private KafkaTemplate<String, ItemOperation> template;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, RECEIVER_TOPIC);

	@BeforeClass
	public static void setup() {
		System.setProperty("kafka.bootstrap-servers", embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Before
	public void setUp() throws Exception {
		// set up the Kafka producer properties
		Map<String, Object> senderProperties = KafkaTestUtils.senderProps(embeddedKafka.getEmbeddedKafka().getBrokersAsString());
		senderProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		senderProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		// create a Kafka producer factory
		ProducerFactory<String, ItemOperation> producerFactory = new DefaultKafkaProducerFactory<String, ItemOperation>(senderProperties);

		// create a Kafka template
		template = new KafkaTemplate<>(producerFactory);
		// set the default topic to send to
		template.setDefaultTopic(RECEIVER_TOPIC);

		// wait until the partitions are assigned
		for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
			ContainerTestUtils.waitForAssignment(messageListenerContainer,embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
		}    

	}

	@Test
	public void whenAnItemOperationIsReceived_thenKafkaConsumerReceivesIt() throws Exception {

		Long itemId = 1L;
		Item item = new Item.Builder().withName("PlayStation4").withId(itemId).withStock(5).build();
		consumer.resetLatch(1);
		RestClient mockRestClient= Mockito.mock(RestClient.class);
		consumer.setRestClient(mockRestClient);
		//DB is mocked
		Mockito.when(consumer.getItemsPersistence().get(itemId)).thenReturn(item);

		ItemOperation itemOp = new ItemOperation(OperationType.CREATED, item);

		template.sendDefault(itemOp);

		LOGGER.debug("test-sender sent message='{}'", itemOp.toString());

		//Wait for the message to be received
		consumer.getLatch().await(1000, TimeUnit.MILLISECONDS);
		
		ArgumentCaptor<Item> itemArg = ArgumentCaptor.forClass(Item.class);
		ArgumentCaptor<Integer> intArg = ArgumentCaptor.forClass(int.class);
		
		verify(mockRestClient).sendPurchaseOrder(itemArg.capture(),intArg.capture());
		assertEquals(item, itemArg.getValue());
		
		// check that the message was received, so the latch is 0
		assertThat(consumer.getLatch().getCount()).isEqualTo(0);
	}
}
