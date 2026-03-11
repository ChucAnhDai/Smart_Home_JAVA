package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.response.NotificationResDTO;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.entity.User;
import org.springframework.data.domain.Page;

public interface NotificationService {
    void createNotification(String message, NotificationType type, User user);
    
    void createGlobalNotification(String message, NotificationType type);

    Page<NotificationResDTO> getNotifications(User user, NotificationType type, Boolean isRead, int page, int size, String sortBy, String sortDir);

    NotificationResDTO markAsRead(Long id, User user);

    void markAllAsRead(User user);
}
