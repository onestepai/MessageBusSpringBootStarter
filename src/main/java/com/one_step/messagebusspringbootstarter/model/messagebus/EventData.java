package com.one_step.messagebusspringbootstarter.model.messagebus;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EventData {
    private String data;
    private String token;
}
