package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.DeviceRealtimeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void handleDeviceStatusUpdate_ShouldReturnUpdatedDTOWithNewTimestamp() throws InterruptedException {
        // Arrange
        Timestamp oldTimestamp = new Timestamp(System.currentTimeMillis() - 10000);
        DeviceRealtimeDTO inputDto = DeviceRealtimeDTO.builder()
                .deviceId(1L)
                .deviceName("Test Device")
                .status("ON")
                .isOnline(true)
                .timestamp(oldTimestamp)
                .build();

        // Act
        DeviceRealtimeDTO result = webSocketController.handleDeviceStatusUpdate(inputDto);

        // Assert
        assertEquals(inputDto.getDeviceId(), result.getDeviceId());
        assertEquals(inputDto.getDeviceName(), result.getDeviceName());
        assertEquals(inputDto.getStatus(), result.getStatus());
        assertEquals(inputDto.getIsOnline(), result.getIsOnline());
        assertNotEquals(oldTimestamp, result.getTimestamp(), "Timestamp should be refreshed by server");
        assertTrue(result.getTimestamp().getTime() >= oldTimestamp.getTime(), "New timestamp should be current");
    }
}
