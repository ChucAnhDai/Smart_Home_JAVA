package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateDeviceReq;
import com.smarthome.nexus.dto.response.DeviceResDTO;
import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.entity.DeviceType;
import com.smarthome.nexus.entity.Room;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.DeviceTypeRepository;
import com.smarthome.nexus.repository.RoomRepository;
import com.smarthome.nexus.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private DeviceTypeRepository deviceTypeRepository;
    
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    private Device device;
    private Room room;
    private DeviceType deviceType;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(1L);
        room.setName("Living Room");

        deviceType = new DeviceType();
        deviceType.setId(1L);
        deviceType.setName("Light");

        device = Device.builder()
                .id(1L)
                .name("Bulb")
                .room(room)
                .deviceType(deviceType)
                .status("OFF")
                .build();
    }

    @Test
    void createDevice_success() {
        CreateDeviceReq req = new CreateDeviceReq("Bulb", 1L, 1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(deviceTypeRepository.findById(1L)).thenReturn(Optional.of(deviceType));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        when(modelMapper.map(any(), any())).thenReturn(new DeviceResDTO());

        DeviceResDTO result = deviceService.createDevice(req);

        assertNotNull(result);
        verify(deviceRepository).save(any(Device.class));
    }

    @Test
    void turnOnDevice_success() {
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        when(modelMapper.map(any(), any())).thenReturn(new DeviceResDTO());

        deviceService.turnOnDevice(1L);

        assertEquals("ON", device.getStatus());
        verify(eventPublisher).publishEvent(any());
        verify(activityLogService).logActivity(anyLong(), anyString(), anyString());
    }

    @Test
    void toggleDevice_success() {
        device.setStatus("OFF");
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        when(modelMapper.map(any(), any())).thenReturn(new DeviceResDTO());

        deviceService.toggleDevice(1L);

        assertEquals("ON", device.getStatus());
    }
}
