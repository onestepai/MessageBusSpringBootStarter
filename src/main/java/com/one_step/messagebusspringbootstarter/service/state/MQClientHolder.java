package com.one_step.messagebusspringbootstarter.service.state;

import com.one_step.messagebusspringbootstarter.service.client.MQClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MQClientHolder {

    private Map<String, MQClient> clientHolder = new HashMap<>();
    private static MQClientHolder mqClientHolder = null;

    public static MQClientHolder getInstance() {
        if (mqClientHolder == null) {
            mqClientHolder = new MQClientHolder();
        }
        return mqClientHolder;
    }

    public String addClient(MQClient mqClient) {
        String clientToken = UUID.randomUUID().toString();
        clientHolder.put(clientToken, mqClient);
        return clientToken;
    }

    public MQClient getMQClient(String clientToken) {
        return clientHolder.getOrDefault(clientToken, null);
    }

    public void removeClient(String clientToken) {
        clientHolder.remove(clientToken);
    }
}
