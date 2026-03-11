package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.response.DashboardStatsDTO;
import com.smarthome.nexus.dto.response.RecentActivityDTO;
import com.smarthome.nexus.repository.*;
import com.smarthome.nexus.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DeviceRepository deviceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;
    private final EnergyMonitoringRepository energyMonitoringRepository;

    public DashboardServiceImpl(
            DeviceRepository deviceRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            ActivityLogRepository activityLogRepository,
            EnergyMonitoringRepository energyMonitoringRepository) {
        this.deviceRepository = deviceRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
        this.energyMonitoringRepository = energyMonitoringRepository;
    }

    @Override
    public DashboardStatsDTO getOverview() {
        return DashboardStatsDTO.builder()
                .totalDevices(deviceRepository.count())
                .devicesOn(deviceRepository.countByStatusIgnoreCase("ON"))
                .devicesOff(deviceRepository.countByStatusIgnoreCase("OFF"))
                .devicesOnline(deviceRepository.countByIsOnline(true))
                .totalRooms(roomRepository.count())
                .totalUsers(userRepository.count())
                .totalEnergyConsumption(energyMonitoringRepository.calculateTotalEnergyConsumption())
                .recentActivities(getRecentActivities(5))
                .build();
    }

    @Override
    public Map<String, Long> getDeviceStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", deviceRepository.count());
        stats.put("on", deviceRepository.countByStatusIgnoreCase("ON"));
        stats.put("off", deviceRepository.countByStatusIgnoreCase("OFF"));
        stats.put("online", deviceRepository.countByIsOnline(true));
        stats.put("offline", deviceRepository.countByIsOnline(false));
        return stats;
    }

    @Override
    public Map<String, Double> getEnergyStats() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("totalEnergyKwh", energyMonitoringRepository.calculateTotalEnergyConsumption());
        // Có thể mở rộng để tính năng lượng trong ngày (today) hoặc tháng (this month)
        return stats;
    }

    @Override
    public List<RecentActivityDTO> getRecentActivities(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return activityLogRepository.findAll(pageable).stream().map(log -> RecentActivityDTO.builder()
                .deviceName(log.getDevice() != null ? log.getDevice().getName() : "System")
                .eventType(log.getEventType())
                .description(log.getDescription())
                .roomName(log.getDevice() != null && log.getDevice().getRoom() != null
                        ? log.getDevice().getRoom().getName()
                        : "System")
                .deviceTypeIcon(log.getDevice() != null && log.getDevice().getDeviceType() != null
                        ? log.getDevice().getDeviceType().getIcon()
                        : "🔔")
                .timestamp(log.getCreatedAt())
                .build()).collect(Collectors.toList());
    }
}
