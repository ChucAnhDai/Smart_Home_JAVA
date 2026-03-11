package com.smarthome.nexus.service.scheduler;

import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.service.EnergyMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnergyTrackingTask {

    private final DeviceRepository deviceRepository;
    private final EnergyMonitoringService energyMonitoringService;

    /**
     * Runs every 60 seconds to calculate and record energy consumption for devices that are ON.
     * Formula: kWh = kW * (60 / 3600) = kW * (1/60)
     */
    @Scheduled(fixedRate = 60000)
    public void trackActiveDevicesEnergy() {
        log.info("--- Energy Tracking Job Started ---");
        
        try {
            List<Device> activeDevices = deviceRepository.findByStatusIgnoreCase("ON");
            
            if (activeDevices == null || activeDevices.isEmpty()) {
                log.info("No active devices found. Skipping...");
                return;
            }

            log.info("Found {} active device(s). Calculating consumption...", activeDevices.size());
            int count = 0;
            for (Device device : activeDevices) {
                Float energyKw = device.getEnergyKw();
                if (energyKw != null && energyKw > 0) {
                    // Calculate consumption for 1 minute (1/60th of an hour)
                    double consumedKwh = energyKw * (1.0 / 60.0);
                    
                    try {
                        energyMonitoringService.recordEnergy(device.getId(), consumedKwh, new java.sql.Timestamp(System.currentTimeMillis()));
                        count++;
                    } catch (Exception e) {
                        log.error("Error recording energy for device '{}' (ID={}): {}", device.getName(), device.getId(), e.getMessage());
                    }
                }
            }
            log.info("--- Energy Tracking Job Finished (Recorded: {} records) ---", count);
        } catch (Exception e) {
            log.error("Critical error in EnergyTrackingTask: {}", e.getMessage(), e);
        }
    }
}
