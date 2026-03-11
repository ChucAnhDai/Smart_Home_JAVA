package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.EnergyMonitoringResDTO;
import com.smarthome.nexus.service.EnergyMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping("/api/energy-monitoring")
@Slf4j
@Validated
public class EnergyMonitoringController {

    private final EnergyMonitoringService energyMonitoringService;

    public EnergyMonitoringController(EnergyMonitoringService energyMonitoringService) {
        this.energyMonitoringService = energyMonitoringService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<EnergyMonitoringResDTO>> getEnergyRecords(
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("REST request to get energy records. Filters: deviceId={}, start={}, end={}", 
                deviceId, startTime, endTime);

        Timestamp start = startTime != null ? new Timestamp(startTime.getTime()) : null;
        Timestamp end = endTime != null ? new Timestamp(endTime.getTime()) : null;

        return ResponseEntity
                .ok(energyMonitoringService.getEnergyRecords(deviceId, start, end, page, size, sortBy, sortDir));
    }
}
