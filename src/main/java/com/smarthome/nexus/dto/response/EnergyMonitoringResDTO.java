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
public class EnergyMonitoringResDTO {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private Double energyKwh;
    private Timestamp recordedAt;
    private Timestamp createdAt;
}
