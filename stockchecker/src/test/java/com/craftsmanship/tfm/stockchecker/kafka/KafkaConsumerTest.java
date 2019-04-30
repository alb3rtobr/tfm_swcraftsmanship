package com.craftsmanship.tfm.stockchecker.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.test.context.junit4.SpringRunner;

import com.craftsmanship.tfm.stockchecker.kafka.model.Item;
import com.craftsmanship.tfm.stockchecker.kafka.model.ItemOperation;
import com.craftsmanship.tfm.stockchecker.kafka.model.OperationType;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class KafkaConsumerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTest.class);

  @Value("${kafka.topic.json}")
  private static String RECEIVER_TOPIC = "mytopic";

  @Autowired
  private KafkaConsumer	consumer;

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
    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
        .getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
    }
  }

  @Test
  public void testReceive() throws Exception {
    // send the message
	Item item= new Item.Builder().withDescription("PS4").build();
	ItemOperation itemOp = new ItemOperation(OperationType.CREATED,item);
	template.sendDefault(itemOp);
    
    LOGGER.debug("test-sender sent message='{}'", itemOp.toString());

    consumer.getLatch().await(10000, TimeUnit.MILLISECONDS);
    // check that the message was received
    assertThat(consumer.getLatch().getCount()).isEqualTo(0);
  }
}
