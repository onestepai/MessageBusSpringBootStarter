package com.one_step.messagebusspringbootstarter.util;

import java.time.Instant;

public class TimeUtil {
    public static Long getCurrentTimestamp() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }
}
