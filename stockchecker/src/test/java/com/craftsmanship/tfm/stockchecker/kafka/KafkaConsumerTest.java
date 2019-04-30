package com.craftsmanship.tfm.stockchecker.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
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
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.stockchecker.kafka.model.Item;
import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;
import com.craftsmanship.tfm.stockchecker.kafka.model.OperationType;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext

public class KafkaConsumerTest {

	public static final String RECEIVER_TOPIC=KafkaConsumer.TOPIC_NAME;

	@Autowired
	private KafkaProducer producer;
	
	@Autowired
	private KafkaConsumer consumer;

	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, "json.t");
	
	@Before
	public void setUp() throws Exception {

		// wait until the partitions are assigned
	    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
	        .getListenerContainers()) {
	      ContainerTestUtils.waitForAssignment(messageListenerContainer,
	          embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
	    }

	}

	@Test
	public void testReceive() throws Exception {
		Item item= new Item.Builder().withDescription("PS4").build();
		ItemOperation itemOp = new ItemOperation(OperationType.CREATED,item);
		producer.send(itemOp);

		consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
		assertThat(consumer.getLatch().getCount()).isEqualTo(0);
	}
}



