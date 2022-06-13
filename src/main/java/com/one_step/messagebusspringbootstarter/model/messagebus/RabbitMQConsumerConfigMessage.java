package com.one_step.messagebusspringbootstarter.model.messagebus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Data
public class RabbitMQConsumerConfigMessage extends ConsumerConfigMessage {
    @JsonProperty("address")
    private String address;

    @JsonProperty("port")
    private String port;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("concurrency")
    private String concurrency;

    @JsonProperty("acknowledgeMode")
    private String acknowledgeMode;

    @JsonProperty("maxConcurrency")
    private String maxConcurrency;

    @JsonProperty("prefetch")
    private String prefetch;

    @JsonProperty("exchangeName")
    private String exchangeName;
}
