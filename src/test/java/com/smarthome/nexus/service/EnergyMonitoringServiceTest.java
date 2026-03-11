package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.EnergyMonitoringResDTO;
import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.entity.EnergyMonitoring;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.EnergyMonitoringRepository;
import com.smarthome.nexus.service.impl.EnergyMonitoringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnergyMonitoringServiceTest {

    @Mock
    private EnergyMonitoringRepository energyMonitoringRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private EnergyMonitoringServiceImpl energyMonitoringService;

    private Device device;
    private EnergyMonitoring energyMonitoring;

    @BeforeEach
    void setUp() {
        device = Device.builder()
                .id(1L)
                .name("Test Device")
                .build();

        energyMonitoring = EnergyMonitoring.builder()
                .id(1L)
                .device(device)
                .energyKwh(1.5)
                .recordedAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    @Test
    void recordEnergy_success() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(energyMonitoringRepository.save(any(EnergyMonitoring.class))).thenReturn(energyMonitoring);

        assertDoesNotThrow(() -> energyMonitoringService.recordEnergy(1L, 1.5, null));

        verify(energyMonitoringRepository).save(any(EnergyMonitoring.class));
    }

    @Test
    void getEnergyRecords_success() {
        Page<EnergyMonitoring> page = new PageImpl<>(Collections.singletonList(energyMonitoring));

        when(energyMonitoringRepository.filterEnergyRecords(eq(1L), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<EnergyMonitoringResDTO> result = energyMonitoringService.getEnergyRecords(1L, null, null, 0, 10, "recordedAt", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1.5, result.getContent().get(0).getEnergyKwh());
    }
}
