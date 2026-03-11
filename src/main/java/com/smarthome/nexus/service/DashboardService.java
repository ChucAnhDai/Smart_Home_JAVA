package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.DashboardStatsDTO;
import com.smarthome.nexus.dto.response.RecentActivityDTO;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    DashboardStatsDTO getOverview();

    Map<String, Long> getDeviceStats();

    Map<String, Double> getEnergyStats();

    List<RecentActivityDTO> getRecentActivities(int limit);
}
