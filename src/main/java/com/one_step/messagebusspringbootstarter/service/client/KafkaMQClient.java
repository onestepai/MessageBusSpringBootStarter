package com.one_step.messagebusspringbootstarter.service.client;

import com.one_step.messagebusspringbootstarter.model.messagebus.EventData;
import com.one_step.messagebusspringbootstarter.model.messagebus.KafkaMQConsumerConfigMessage;
import com.one_step.messagebusspringbootstarter.model.messagebus.RabbitMQConsumerConfigMessage;
import com.rabbitmq.client.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class KafkaMQClient implements MQClient {

    private Logger logger = LoggerFactory.getLogger(KafkaMQClient.class);

    private ConcurrentMessageListenerContainer container;

    public static KafkaMQClient newClient(KafkaMQConsumerConfigMessage configMessage, String topicKey) throws IOException, TimeoutException {
        KafkaMQClient kafkaMQClient = new KafkaMQClient();
        kafkaMQClient.createContainer(configMessage, topicKey);
        return kafkaMQClient;
    }

    public void startListen(Consumer<EventData> consumer, String token) throws IOException {
        this.createBasicConsume(consumer, token);
        this.container.start();
    }

    private void createContainer(KafkaMQConsumerConfigMessage configMessage, final String topicKey) throws IOException, TimeoutException {
        Map consumerProperties = new HashMap<>();
        final String serverHost =  configMessage.getAddress().strip() + ":" +  configMessage.getPort().strip();

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverHost);

        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, configMessage.getAutoCommit());
        DefaultKafkaConsumerFactory factory =
                new DefaultKafkaConsumerFactory(consumerProperties,new StringDeserializer(),new StringDeserializer());

        ContainerProperties properties = new ContainerProperties(topicKey);

        properties.setGroupId(configMessage.getGroupName());

        properties.setAckMode(ContainerProperties.AckMode.RECORD);

        container = new ConcurrentMessageListenerContainer(factory,properties);

        container.setConcurrency(configMessage.getMaxConcurrency());
        this.logger.info("RabbitMQ connection creation successfully");
    }


    private void createBasicConsume(Consumer<EventData> consumer, String token) throws IOException {
        container.setupMessageListener((MessageListener<String, String>) (consumerRecord) -> {
            consumer.accept(new EventData(consumerRecord.value(), token));
        });
    }

    @Override
    public void stopListen() throws IOException, TimeoutException {
        this.container.stop();
    }
}
