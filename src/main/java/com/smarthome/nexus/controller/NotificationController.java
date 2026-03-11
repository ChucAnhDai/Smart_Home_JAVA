package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.NotificationResDTO;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResDTO>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("Fetching notifications for user: {}, type: {}, isRead: {}", user.getEmail(), type, isRead);
        return ResponseEntity.ok(notificationService.getNotifications(user, type, isRead, page, size, sortBy, sortDir));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResDTO> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        log.info("Marking notification {} as read for user: {}", id, user.getEmail());
        return ResponseEntity.ok(notificationService.markAsRead(id, user));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        log.info("Marking all notifications as read for user: {}", user.getEmail());
        notificationService.markAllAsRead(user);
        return ResponseEntity.noContent().build();
    }
}
