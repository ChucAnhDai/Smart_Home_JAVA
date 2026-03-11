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
public class ActivityLogResDTO {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private String eventType;
    private String description;
    private Timestamp createdAt;
}
