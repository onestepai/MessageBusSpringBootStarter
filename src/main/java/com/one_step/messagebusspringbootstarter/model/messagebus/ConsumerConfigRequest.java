package com.one_step.messagebusspringbootstarter.model.messagebus;

import lombok.Data;

@Data
public class ConsumerConfigRequest {
    private String clientId;
    private String requestId;
    private Long timestamp;
    private String topicKey;
}
