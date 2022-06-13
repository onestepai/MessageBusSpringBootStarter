package com.one_step.messagebusspringbootstarter.service;

import com.one_step.messagebusspringbootstarter.model.MessageBusListenResult;
import com.one_step.messagebusspringbootstarter.model.MessageBusResult;
import com.one_step.messagebusspringbootstarter.model.messagebus.EventData;
import com.one_step.messagebusspringbootstarter.service.client.MessageBusClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class MessageBusTemplate {

    private Logger logger = LoggerFactory.getLogger(MessageBusTemplate.class);
    private String url;
    private String clientId;
    private MessageBusClient messageBusClient;

    public MessageBusTemplate(String url, String clientId) {
        this.url = url;
        this.clientId = clientId;
        messageBusClient = new MessageBusClient();
    }

    public MessageBusResult sendMessage(String topicKey, String message) {
        this.logger.info(String.format("send message topicKey: %s", topicKey));
        try {
            return messageBusClient.sendMessage(this.url, this.clientId, topicKey, message);
        } catch (IOException e) {
            MessageBusResult result = new MessageBusResult();
            result.setIsSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    public MessageBusListenResult listen(String topicKey, Consumer<EventData> consumer) {
       return this.messageBusClient.startListen(this.url, this.clientId, topicKey, consumer);
    }

    public MessageBusResult stopListen(String token) {
        return this.messageBusClient.stopListen(token);
    }
}
