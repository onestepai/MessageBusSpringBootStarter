package com.one_step.messagebusspringbootstarter.model.messagebus;

import lombok.Data;

@Data
public class SendMessageResponse {
    private Integer errorCode;
    private String errorMessage;
    private String requestId;
    private Long timestamp;
}
