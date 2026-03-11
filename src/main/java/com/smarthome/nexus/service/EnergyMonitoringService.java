package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.EnergyMonitoringResDTO;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface EnergyMonitoringService {
    void recordEnergy(Long deviceId, Double energyKwh, Timestamp recordedAt);

    Page<EnergyMonitoringResDTO> getEnergyRecords(Long deviceId, Timestamp startTime, Timestamp endTime, int page,
            int size, String sortBy, String sortDir);
}
