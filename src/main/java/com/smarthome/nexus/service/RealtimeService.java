package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.DeviceRealtimeDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class RealtimeService {

    private final SimpMessagingTemplate messagingTemplate;

    public RealtimeService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast trạng thái thiết bị tới tất cả các client đang subscribe vào topic
     * /topic/device-status
     */
    public void broadcastDeviceStatus(Long deviceId, String deviceName, String status, Boolean isOnline) {
        DeviceRealtimeDTO dto = DeviceRealtimeDTO.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .status(status)
                .isOnline(isOnline)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        messagingTemplate.convertAndSend("/topic/device-status", dto);
    }

    /**
     * Phương thức đa dụng có thể gửi bất kỳ object nào đến topic tùy chỉnh
     */
    public void sendRealtimeUpdate(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}
