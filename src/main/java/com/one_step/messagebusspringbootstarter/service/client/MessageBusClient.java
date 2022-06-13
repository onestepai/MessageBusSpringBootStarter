package com.one_step.messagebusspringbootstarter.service.client;

import com.one_step.messagebusspringbootstarter.model.MessageBusListenResult;
import com.one_step.messagebusspringbootstarter.model.MessageBusResult;
import com.one_step.messagebusspringbootstarter.model.messagebus.*;
import com.one_step.messagebusspringbootstarter.service.state.MQClientHolder;
import com.one_step.messagebusspringbootstarter.util.JsonUtils;
import com.one_step.messagebusspringbootstarter.util.TimeUtil;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class MessageBusClient {

    private Logger logger = LoggerFactory.getLogger(MessageBusClient.class);
    private final static String CONTEXT_PATH_SEND_MESSAGE = "message/sendMessage";
    private final static String CONTEXT_PATH_GET_CONSUMER_CONFIG = "/message/getConsumerConfig";

    public MessageBusResult sendMessage(String url, String clientId, String topicKey, String message) throws IOException {
        MessageBusResult result = new MessageBusResult();
        String sendMessageUrl = "";

        try {
            sendMessageUrl = this.concatUrl(url, CONTEXT_PATH_SEND_MESSAGE);
        } catch (URISyntaxException e) {
            result.setIsSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setClientId(clientId);
        sendMessageRequest.setTopicKey(topicKey);
        sendMessageRequest.setMessageContent(message);
        sendMessageRequest.setTimestamp(TimeUtil.getCurrentTimestamp());
        sendMessageRequest.setRequestId(UUID.randomUUID().toString());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(sendMessageUrl);
        StringEntity entity = new StringEntity(JsonUtils.writeJson(sendMessageRequest), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            this.logger.info(String.format("message response code: %s", response.getStatusLine().getStatusCode()));
            if (response.getStatusLine().getStatusCode() != 200) {
                result.setIsSuccess(false);
                result.setMessage(response.getStatusLine().getReasonPhrase());
                return result;
            }

            String responseJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            SendMessageResponse responseObj = JsonUtils.readJson(responseJson, SendMessageResponse.class);

            result.setIsSuccess(responseObj.getErrorCode() == 0);
            result.setMessage(responseObj.getErrorMessage());
        } catch (Exception e) {
            this.logger.info(String.format("message send exception %s", e.getMessage()));
            result.setIsSuccess(false);
            result.setMessage(e.getMessage());
        } finally {
            client.close();
        }
        return result;
    }

    public MessageBusListenResult startListen(String url, String clientId, String topicKey, Consumer<EventData> consumer) {
        MessageBusListenResult result = new MessageBusListenResult();
        try {
            String sendMessageUrl = "";

            try {
                sendMessageUrl = this.concatUrl(url, CONTEXT_PATH_GET_CONSUMER_CONFIG);
            } catch (URISyntaxException e) {
                result.setIsSuccess(false);
                result.setMessage(e.getMessage());
                return result;
            }

            // get MQ connect string from message bus
            ConsumerConfigResponse response = this.getConsumerConfig(sendMessageUrl, clientId, topicKey);
            if (response != null) {
                ConsumerConfigMessage configMessage = response.getConsumerConfig();
                if (configMessage instanceof RabbitMQConsumerConfigMessage) {
                    RabbitMQClient client = RabbitMQClient.newClient((RabbitMQConsumerConfigMessage) configMessage, topicKey);
                    String token = MQClientHolder.getInstance().addClient(client);
                    client.startListen(consumer, token);

                    result.setClientToken(token);
                    result.setIsSuccess(true);
                    result.setMessage("");
                    return result;
                } else if (configMessage instanceof KafkaMQConsumerConfigMessage) {
                    KafkaMQClient client = KafkaMQClient.newClient((KafkaMQConsumerConfigMessage) configMessage, topicKey);
                    String token = MQClientHolder.getInstance().addClient(client);
                    client.startListen(consumer, token);

                    result.setClientToken(token);
                    result.setIsSuccess(true);
                    result.setMessage("");
                    return result;
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                result.setIsSuccess(false);
                result.setMessage("cannot get consumer config");
                return result;
            }
        } catch (IOException | TimeoutException e) {
            result.setIsSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
    }

    public ConsumerConfigResponse getConsumerConfig(String url, String clientId, String topicKey) throws IOException {
        ConsumerConfigRequest consumerConfigRequest = new ConsumerConfigRequest();
        consumerConfigRequest.setClientId(clientId);
        consumerConfigRequest.setRequestId(UUID.randomUUID().toString());
        consumerConfigRequest.setTimestamp(TimeUtil.getCurrentTimestamp());
        consumerConfigRequest.setTopicKey(topicKey);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(JsonUtils.writeJson(consumerConfigRequest), "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try {
            CloseableHttpResponse response = client.execute(httpPost);
            this.logger.info(String.format("get connection response code: %s", response.getStatusLine().getStatusCode()));
            String responseJson = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            return JsonUtils.readJson(responseJson, ConsumerConfigResponse.class);
        } catch (Exception e) {
            this.logger.info(String.format("get connection exception %s", e.getMessage()));
        } finally {
            client.close();
        }
        return null;
    }

    public MessageBusResult stopListen(String token) {
        MQClient client = MQClientHolder.getInstance().getMQClient(token);
        if (client != null) {
            try {
                client.stopListen();
                MQClientHolder.getInstance().removeClient(token);
            } catch (Exception e) {
                MessageBusResult result = new MessageBusResult();
                result.setIsSuccess(false);
                result.setMessage(e.getMessage());
                return result;
            }
        }
        MessageBusResult result = new MessageBusResult();
        result.setIsSuccess(true);
        result.setMessage("");
        return result;
    }

    private String concatUrl(String baseUrl, String path) throws URISyntaxException {
        return URI.create(baseUrl + "/" + path).normalize().toString();
    }
}
