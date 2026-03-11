package com.smarthome.nexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRealtimeDTO {
    private Long deviceId;
    private String deviceName;
    private String status; // ON / OFF
    private Boolean isOnline;
    private Timestamp timestamp;
}
