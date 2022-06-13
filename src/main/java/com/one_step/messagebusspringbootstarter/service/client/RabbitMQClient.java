package com.one_step.messagebusspringbootstarter.service.client;

import com.one_step.messagebusspringbootstarter.model.messagebus.EventData;
import com.one_step.messagebusspringbootstarter.model.messagebus.RabbitMQConsumerConfigMessage;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class RabbitMQClient implements MQClient {

    private Logger logger = LoggerFactory.getLogger(RabbitMQClient.class);

    private Connection connection;
    private Channel channel;
    private String queueName;

    public static RabbitMQClient newClient(RabbitMQConsumerConfigMessage configMessage, String topicKey) throws IOException, TimeoutException {
        RabbitMQClient rabbitMQClient = new RabbitMQClient();
        rabbitMQClient.createConnection(configMessage);
        rabbitMQClient.createChannel(rabbitMQClient.connection, configMessage, topicKey);
        return rabbitMQClient;
    }

    public void startListen(Consumer<EventData> consumer, String token) throws IOException {
        this.createBasicConsume(this.channel, consumer, token);
    }

    private void createConnection(RabbitMQConsumerConfigMessage configMessage) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(configMessage.getUsername());
        factory.setPassword(configMessage.getPassword());
        factory.setHost(configMessage.getAddress());
        factory.setPort(Integer.parseInt(configMessage.getPort()));

        // factory.setHost("139.155.89.168");
        // factory.setPort(30672);
        this.connection = factory.newConnection();
        this.logger.info("RabbitMQ connection creation successfully");
    }

    private void createChannel(Connection connection, RabbitMQConsumerConfigMessage configMessage, String routingKey) throws IOException {
        this.channel = connection.createChannel();
        this.queueName = this.channel.queueDeclare().getQueue();
        channel.queueBind(queueName, configMessage.getExchangeName(), routingKey);
        this.logger.info("RabbitMQ channel creation successfully");
    }

    private void createBasicConsume(Channel channel, Consumer<EventData> consumer, String token) throws IOException {
        channel.basicConsume(this.queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) {
                String message = new String(body, StandardCharsets.UTF_8);
                consumer.accept(new EventData(message, token));
            }
        });
    }

    @Override
    public void stopListen() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }
}
