package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.response.NotificationResDTO;
import com.smarthome.nexus.entity.Notification;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.NotificationRepository;
import com.smarthome.nexus.service.NotificationService;
import com.smarthome.nexus.service.RealtimeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final RealtimeService realtimeService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper modelMapper,
            RealtimeService realtimeService) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
        this.realtimeService = realtimeService;
    }

    @Override
    public void createNotification(String message, NotificationType type, User user) {
        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .isRead(false)
                .user(user)
                .build();
        Notification saved = notificationRepository.save(notification);

        // Broadcast realtime to specific user (can be enhanced to use user-specific topic)
        String destination = (user != null) ? "/topic/notifications/" + user.getId() : "/topic/notifications";
        realtimeService.sendRealtimeUpdate(destination, modelMapper.map(saved, NotificationResDTO.class));
        log.info("Notification created for user {}: {}", user != null ? user.getId() : "GLOBAL", message);
    }

    @Override
    public void createGlobalNotification(String message, NotificationType type) {
        createNotification(message, type, null);
    }

    @Override
    public Page<NotificationResDTO> getNotifications(User user, NotificationType type, Boolean isRead, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Notification> notifications = notificationRepository.filterNotifications(user, type, isRead, pageable);
        return notifications.map(notification -> modelMapper.map(notification, NotificationResDTO.class));
    }

    @Override
    public NotificationResDTO markAsRead(Long id, User user) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Security check
        if (notification.getUser() != null && (user == null || !notification.getUser().getId().equals(user.getId()))) {
            throw new RuntimeException("Unauthorized to mark this notification as read");
        }

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Notification {} marked as read", id);

        return modelMapper.map(updatedNotification, NotificationResDTO.class);
    }

    @Override
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsRead(user);
        log.info("All notifications marked as read for user {}", user != null ? user.getId() : "GLOBAL");
    }
}
