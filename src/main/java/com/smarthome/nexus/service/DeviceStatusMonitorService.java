package com.smarthome.nexus.service;

import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceStatusMonitorService {

    private final DeviceRepository deviceRepository;
    private final NotificationService notificationService;

    public DeviceStatusMonitorService(DeviceRepository deviceRepository, NotificationService notificationService) {
        this.deviceRepository = deviceRepository;
        this.notificationService = notificationService;
    }

    /**
     * Kiểm tra trạng thái online/offline của thiết bị mỗi 5 phút.
     * Ở môi trường giả lập, chúng ta check field isOnline.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void monitorOfflineDevices() {
        log.info("Monitoring device statuses...");
        List<Device> offlineDevices = deviceRepository.findAll().stream()
                .filter(d -> !d.getIsOnline())
                .toList();

        for (Device device : offlineDevices) {
            // Chỉ thông báo nếu chưa có thông báo gần đây (tùy chọn)
            notificationService.createGlobalNotification(
                    "Device '" + device.getName() + "' (ID: " + device.getId() + ") is offline.",
                    NotificationType.CRITICAL);
        }
    }
}
