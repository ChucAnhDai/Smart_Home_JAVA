package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.response.EnergyMonitoringResDTO;
import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.entity.EnergyMonitoring;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.EnergyMonitoringRepository;
import com.smarthome.nexus.service.EnergyMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@Slf4j
public class EnergyMonitoringServiceImpl implements EnergyMonitoringService {

    private final EnergyMonitoringRepository energyMonitoringRepository;
    private final DeviceRepository deviceRepository;

    public EnergyMonitoringServiceImpl(EnergyMonitoringRepository energyMonitoringRepository,
            DeviceRepository deviceRepository) {
        this.energyMonitoringRepository = energyMonitoringRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public void recordEnergy(Long deviceId, Double energyKwh, Timestamp recordedAt) {
        log.info("Recording energy for device {}: {} kWh", deviceId, energyKwh);
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        EnergyMonitoring record = EnergyMonitoring.builder()
                .device(device)
                .energyKw(device.getEnergyKw() != null ? device.getEnergyKw() : 0.0f)
                .energyKwh(energyKwh)
                .recordedAt(recordedAt != null ? recordedAt : new Timestamp(System.currentTimeMillis()))
                .build();

        energyMonitoringRepository.save(record);
        log.info("Successfully recorded energy for device {} ({}kW -> {}kWh)", device.getName(), device.getEnergyKw(), energyKwh);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnergyMonitoringResDTO> getEnergyRecords(Long deviceId, Timestamp startTime, Timestamp endTime,
            int page, int size, String sortBy, String sortDir) {
        log.info("Fetching energy records with params: deviceId={}, startTime={}, endTime={}, page={}, size={}",
                deviceId, startTime, endTime, page, size);
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EnergyMonitoring> records = energyMonitoringRepository.filterEnergyRecords(deviceId, startTime, endTime,
                pageable);

        return records.map(record -> EnergyMonitoringResDTO.builder()
                .id(record.getId())
                .deviceId(record.getDevice().getId())
                .deviceName(record.getDevice().getName())
                .energyKwh(record.getEnergyKwh())
                .recordedAt(record.getRecordedAt())
                .createdAt(record.getCreatedAt())
                .build());
    }
}
