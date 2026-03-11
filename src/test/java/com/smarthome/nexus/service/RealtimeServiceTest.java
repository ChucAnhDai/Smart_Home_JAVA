package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.DeviceRealtimeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RealtimeServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private RealtimeService realtimeService;

    @Test
    void broadcastDeviceStatus_ShouldSendMessageToCorrectTopic() {
        // Arrange
        Long deviceId = 1L;
        String name = "Light";
        String status = "ON";
        Boolean isOnline = true;

        // Act
        realtimeService.broadcastDeviceStatus(deviceId, name, status, isOnline);

        // Assert
        ArgumentCaptor<DeviceRealtimeDTO> captor = ArgumentCaptor.forClass(DeviceRealtimeDTO.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/device-status"), captor.capture());

        DeviceRealtimeDTO sentDto = captor.getValue();
        assertEquals(deviceId, sentDto.getDeviceId());
        assertEquals(name, sentDto.getDeviceName());
        assertEquals(status, sentDto.getStatus());
        assertEquals(isOnline, sentDto.getIsOnline());
        assertNotNull(sentDto.getTimestamp());
    }

    @Test
    void sendRealtimeUpdate_ShouldSendPayloadToCustomTopic() {
        // Arrange
        String topic = "/topic/custom";
        String payload = "Hello World";

        // Act
        realtimeService.sendRealtimeUpdate(topic, payload);

        // Assert
        verify(messagingTemplate).convertAndSend(topic, payload);
    }
}
