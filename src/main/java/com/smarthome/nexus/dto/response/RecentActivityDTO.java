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
public class RecentActivityDTO {
    private String deviceName;
    private String eventType;
    private String description;
    private String roomName;
    private String deviceTypeIcon;
    private Timestamp timestamp;
}
