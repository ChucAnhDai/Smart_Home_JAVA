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
public class DeviceResDTO {
    private Long id;
    private String name;
    private String status;
    private Boolean isOnline;
    private Float energyKw;
    private Timestamp lastActiveTime;
    private Timestamp createdAt;

    // Nested DTOs
    private RoomResDTO room;
    private DeviceTypeResDTO deviceType;
}
