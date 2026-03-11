package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.DashboardStatsDTO;
import com.smarthome.nexus.dto.response.RecentActivityDTO;
import com.smarthome.nexus.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<DashboardStatsDTO> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview());
    }

    @GetMapping("/devices")
    public ResponseEntity<Map<String, Long>> getDeviceStats() {
        return ResponseEntity.ok(dashboardService.getDeviceStats());
    }

    @GetMapping("/energy")
    public ResponseEntity<Map<String, Double>> getEnergyStats() {
        return ResponseEntity.ok(dashboardService.getEnergyStats());
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<RecentActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getRecentActivities(limit));
    }
}
