package com.smarthome.nexus.service.scheduler;

import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.service.EnergyMonitoringService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyTrackingTaskTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private EnergyMonitoringService energyMonitoringService;

    @InjectMocks
    private EnergyTrackingTask energyTrackingTask;

    @Test
    void trackActiveDevicesEnergy_withActiveDevices_shouldRecordEnergy() {
        // Arrange
        Device device1 = Device.builder().id(1L).status("ON").energyKw(1.5f).build();
        Device device2 = Device.builder().id(2L).status("ON").energyKw(0.5f).build();
        List<Device> activeDevices = Arrays.asList(device1, device2);

        when(deviceRepository.findByStatusIgnoreCase("ON")).thenReturn(activeDevices);

        // Act
        energyTrackingTask.trackActiveDevicesEnergy();

        // Assert
        verify(energyMonitoringService, times(1)).recordEnergy(eq(1L), eq(1.5 / 60.0), any(Timestamp.class));
        verify(energyMonitoringService, times(1)).recordEnergy(eq(2L), eq(0.5 / 60.0), any(Timestamp.class));
    }

    @Test
    void trackActiveDevicesEnergy_withNoActiveDevices_shouldNotRecordEnergy() {
        // Arrange
        when(deviceRepository.findByStatusIgnoreCase("ON")).thenReturn(Collections.emptyList());

        // Act
        energyTrackingTask.trackActiveDevicesEnergy();

        // Assert
        verify(energyMonitoringService, never()).recordEnergy(anyLong(), anyDouble(), any(Timestamp.class));
    }

    @Test
    void trackActiveDevicesEnergy_withZeroEnergyKw_shouldNotRecordEnergy() {
        // Arrange
        Device device = Device.builder().id(1L).status("ON").energyKw(0.0f).build();
        when(deviceRepository.findByStatusIgnoreCase("ON")).thenReturn(Collections.singletonList(device));

        // Act
        energyTrackingTask.trackActiveDevicesEnergy();

        // Assert
        verify(energyMonitoringService, never()).recordEnergy(anyLong(), anyDouble(), any(Timestamp.class));
    }
}
