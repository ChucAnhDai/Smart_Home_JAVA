package com.smarthome.nexus.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtils {

    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC")));
    }
}
