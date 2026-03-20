package com.smarthome.nexus.service;

import com.smarthome.nexus.entity.*;
import com.smarthome.nexus.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CascadeDeleteIntegrationTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EnergyMonitoringRepository energyMonitoringRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private Long roomId;
    private Long typeId;
    private Long deviceId;

    @BeforeEach
    void setUp() {
        // 1. Create a Room
        Room room = Room.builder().name("Test Room").icon("🏠").build();
        room = roomRepository.save(room);
        roomId = room.getId();

        // 2. Create a DeviceType
        DeviceType type = DeviceType.builder().name("Test Type").icon("⚡").build();
        type = deviceTypeRepository.save(type);
        typeId = type.getId();

        // 3. Create a Device
        Device device = Device.builder()
                .name("Test Device")
                .room(room)
                .deviceType(type)
                .status("OFF")
                .isOnline(true)
                .build();
        device = deviceRepository.save(device);
        deviceId = device.getId();

        // 4. Create Energy Monitoring Data
        EnergyMonitoring em = EnergyMonitoring.builder()
                .device(device)
                .energyKw(1.5f)
                .build();
        energyMonitoringRepository.save(em);

        // 5. Create Activity Log
        ActivityLog log = ActivityLog.builder()
                .device(device)
                .eventType("TEST_EVENT")
                .description("Test description")
                .build();
        activityLogRepository.save(log);
    }

    @Test
    void testDeleteDevice_ShouldCascadeToLogsAndMonitoring() {
        // When
        deviceService.deleteDevice(deviceId);

        // Then
        assertFalse(deviceRepository.findById(deviceId).isPresent());
        assertEquals(0, energyMonitoringRepository.findAll().stream()
                .filter(em -> em.getDevice() != null && em.getDevice().getId().equals(deviceId))
                .count());
        assertEquals(0, activityLogRepository.findAll().stream()
                .filter(log -> log.getDevice() != null && log.getDevice().getId().equals(deviceId))
                .count());
    }

    @Test
    void testDeleteRoom_ShouldCascadeToDevices() {
        // When
        roomService.deleteRoom(roomId);

        // Then
        assertFalse(roomRepository.findById(roomId).isPresent());
        assertFalse(deviceRepository.findById(deviceId).isPresent());
    }

    @Test
    void testDeleteDeviceType_ShouldCascadeToDevices() {
        // When
        deviceTypeService.deleteDeviceType(typeId);

        // Then
        assertFalse(deviceTypeRepository.findById(typeId).isPresent());
        assertFalse(deviceRepository.findById(deviceId).isPresent());
    }
}
