package com.one_step.messagebusspringbootstarter.model.messagebus;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String clientId;
    private String messageContent;
    private String requestId;
    private String topicKey;
    private Long timestamp;
}
