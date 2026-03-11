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
public class RoomResDTO {
    private Long id;
    private String name;
    private String icon;
    private Integer totalDevices;
    private Integer activeDevices;
    private Double energyUsage;
    private Timestamp createdAt;
}
