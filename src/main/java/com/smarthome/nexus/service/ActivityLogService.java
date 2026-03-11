package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.ActivityLogResDTO;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface ActivityLogService {
    void logActivity(Long deviceId, String eventType, String description);

    Page<ActivityLogResDTO> getActivityLogs(Long deviceId, Timestamp startTime, Timestamp endTime, int page, int size,
            String sortBy, String sortDir);
}
