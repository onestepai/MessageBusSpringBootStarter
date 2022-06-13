package com.one_step.messagebusspringbootstarter.service.client;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface MQClient {
    void stopListen() throws IOException, TimeoutException;
}
