package com.smarthome.nexus.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.entity.AutomationRule;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.event.DeviceStatusChangedEvent;
import com.smarthome.nexus.repository.AutomationRuleRepository;
import com.smarthome.nexus.service.AutomationExecutorService;
import com.smarthome.nexus.service.DeviceService;
import com.smarthome.nexus.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class AutomationExecutorServiceImpl implements AutomationExecutorService {

    private final AutomationRuleRepository automationRuleRepository;
    private final DeviceService deviceService;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public AutomationExecutorServiceImpl(AutomationRuleRepository automationRuleRepository,
            DeviceService deviceService,
            ObjectMapper objectMapper,
            NotificationService notificationService) {
        this.automationRuleRepository = automationRuleRepository;
        this.deviceService = deviceService;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 * * * * *") // Every minute
    public void checkTimeBasedRules() {
        String currentTime = LocalTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ofPattern("HH:mm"));
        log.info("Checking time-based automation rules for: {} (Vietnam Time)", currentTime);
        executeRulesForTime(currentTime);
    }

    @EventListener
    public void handleDeviceStatusChanged(DeviceStatusChangedEvent event) {
        log.info("Device status changed event received: {} -> {}", event.getDeviceId(), event.getNewStatus());
        executeRulesForDeviceStatus(event.getDeviceId(), event.getNewStatus());
    }

    @Override
    public void executeRulesForTime(String time) {
        List<AutomationRule> activeRules = automationRuleRepository.findByIsActiveTrue();
        for (AutomationRule rule : activeRules) {
            try {
                JsonNode condition = objectMapper.readTree(rule.getConditionText());
                if ("TIME".equals(condition.get("type").asText()) && time.equals(condition.get("value").asText())) {
                    executeRule(rule);
                }
            } catch (Exception e) {
                log.error("Error executing time rule {}: {}", rule.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void executeRulesForDeviceStatus(Long deviceId, String newStatus) {
        List<AutomationRule> activeRules = automationRuleRepository.findByIsActiveTrue();
        for (AutomationRule rule : activeRules) {
            try {
                JsonNode condition = objectMapper.readTree(rule.getConditionText());
                if ("DEVICE_STATUS".equals(condition.get("type").asText())) {
                    Long targetDeviceId = condition.get("deviceId").asLong();
                    String targetStatus = condition.get("status").asText();
                    if (deviceId.equals(targetDeviceId) && newStatus.equalsIgnoreCase(targetStatus)) {
                        executeRule(rule);
                    }
                }
            } catch (Exception e) {
                log.error("Error executing device status rule {}: {}", rule.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void executeRule(AutomationRule rule) {
        log.info("Executing automation rule: {}", rule.getName());
        try {
            JsonNode action = objectMapper.readTree(rule.getActionText());
            String actionType = action.get("type").asText();
            Long targetDeviceId = action.get("deviceId").asLong();

            if ("TOGGLE_DEVICE".equals(actionType)) {
                String cmdStatus = action.get("status").asText();
                if ("ON".equalsIgnoreCase(cmdStatus)) {
                    deviceService.turnOnDevice(targetDeviceId);
                } else {
                    deviceService.turnOffDevice(targetDeviceId);
                }

                // Create Notification
                notificationService.createGlobalNotification(
                        "Automation Rule '" + rule.getName() + "' executed successfully.",
                        NotificationType.AUTOMATION);
            }
        } catch (Exception e) {
            log.error("Failed to perform action for rule {}: {}", rule.getId(), e.getMessage());
        }
    }
}
