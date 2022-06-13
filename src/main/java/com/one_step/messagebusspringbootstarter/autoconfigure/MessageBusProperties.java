package com.one_step.messagebusspringbootstarter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "messagebus")
public class MessageBusProperties {
    private String url = "http://message-bus";
    private String clientId;
}
