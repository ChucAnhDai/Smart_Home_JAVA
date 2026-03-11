package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.DeviceRealtimeDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;

@Controller
public class WebSocketController {

    /**
     * Client có thể gửi dữ liệu trực tiếp vào /app/device-status
     * Hệ thống sẽ tiếp nhận và public lại cho những client khác qua
     * /topic/device-status
     */
    @MessageMapping("/device-status")
    @SendTo("/topic/device-status")
    public DeviceRealtimeDTO handleDeviceStatusUpdate(DeviceRealtimeDTO updateRequest) {
        // Cập nhật lại thời gian server khi nhận được gói tin để đảm bảo tính đồng bộ
        updateRequest.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return updateRequest;
    }
}
