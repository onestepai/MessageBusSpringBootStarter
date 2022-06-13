package com.one_step.messagebusspringbootstarter.model;

import lombok.Data;

@Data
public class MessageBusListenResult extends MessageBusResult {
    private String clientToken = null;
}
