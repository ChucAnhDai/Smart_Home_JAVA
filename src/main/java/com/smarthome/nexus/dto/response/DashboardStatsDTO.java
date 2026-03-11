package com.smarthome.nexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalDevices;
    private long devicesOn;
    private long devicesOff;
    private long devicesOnline;
    private long totalRooms;
    private long totalUsers;
    private Double totalEnergyConsumption;
    private List<RecentActivityDTO> recentActivities;
}
