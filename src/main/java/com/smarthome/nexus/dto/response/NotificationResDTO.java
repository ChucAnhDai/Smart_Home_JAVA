package com.smarthome.nexus.dto.response;

import com.smarthome.nexus.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private Timestamp createdAt;
}
