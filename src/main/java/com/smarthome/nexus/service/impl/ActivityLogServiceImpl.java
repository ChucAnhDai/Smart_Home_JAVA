package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.response.ActivityLogResDTO;
import com.smarthome.nexus.entity.ActivityLog;
import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.repository.ActivityLogRepository;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final DeviceRepository deviceRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, DeviceRepository deviceRepository) {
        this.activityLogRepository = activityLogRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void logActivity(Long deviceId, String eventType, String description) {
        Device device = null;
        if (deviceId != null) {
            device = deviceRepository.findById(deviceId).orElse(null);
        }

        ActivityLog log = ActivityLog.builder()
                .device(device)
                .eventType(eventType)
                .description(description)
                .build();

        activityLogRepository.save(log);
    }

    @Override
    public Page<ActivityLogResDTO> getActivityLogs(Long deviceId, Timestamp startTime, Timestamp endTime, int page,
            int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ActivityLog> logs = activityLogRepository.filterLogs(deviceId, startTime, endTime, pageable);

        return logs.map(log -> ActivityLogResDTO.builder()
                .id(log.getId())
                .deviceId(log.getDevice() != null ? log.getDevice().getId() : null)
                .deviceName(log.getDevice() != null ? log.getDevice().getName() : null)
                .eventType(log.getEventType())
                .description(log.getDescription())
                .createdAt(log.getCreatedAt())
                .build());
    }
}
