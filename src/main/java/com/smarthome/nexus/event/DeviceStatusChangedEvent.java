package com.smarthome.nexus.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceStatusChangedEvent extends ApplicationEvent {
    private final Long deviceId;
    private final String newStatus;

    public DeviceStatusChangedEvent(Object source, Long deviceId, String newStatus) {
        super(source);
        this.deviceId = deviceId;
        this.newStatus = newStatus;
    }
}
