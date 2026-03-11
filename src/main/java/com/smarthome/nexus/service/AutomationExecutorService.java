package com.smarthome.nexus.service;

import com.smarthome.nexus.entity.AutomationRule;

public interface AutomationExecutorService {
    void executeRulesForTime(String time);

    void executeRulesForDeviceStatus(Long deviceId, String newStatus);

    void executeRule(AutomationRule rule);
}
