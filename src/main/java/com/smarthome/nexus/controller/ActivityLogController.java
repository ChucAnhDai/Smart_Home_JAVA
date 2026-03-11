package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.ActivityLogResDTO;
import com.smarthome.nexus.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ResponseEntity<Page<ActivityLogResDTO>> getActivityLogs(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Timestamp start = startTime != null ? new Timestamp(startTime.getTime()) : null;
        Timestamp end = endTime != null ? new Timestamp(endTime.getTime()) : null;

        return ResponseEntity.ok(activityLogService.getActivityLogs(deviceId, start, end, page, size, sortBy, sortDir));
    }
}
