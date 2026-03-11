package com.smarthome.nexus.service;

import com.smarthome.nexus.entity.Notification;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.repository.NotificationRepository;
import com.smarthome.nexus.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RealtimeService realtimeService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void testCreateNotification_Success() {
        // Arrange
        String message = "Test notification";
        NotificationType type = NotificationType.INFO;
        Notification savedNotification = Notification.builder().id(1L).message(message).type(type).build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Act
        notificationService.createNotification(message, type, null);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(realtimeService, times(1)).sendRealtimeUpdate(eq("/topic/notifications"), any());
    }

    @Test
    public void testGetNotifications_Success() {
        // Arrange
        com.smarthome.nexus.entity.User user = new com.smarthome.nexus.entity.User();
        when(notificationRepository.filterNotifications(eq(user), isNull(), isNull(), any(Pageable.class))).thenReturn(Page.empty());
        // Act
        notificationService.getNotifications(user, null, null, 0, 10, "id", "desc");

        // Assert
        verify(notificationRepository, times(1)).filterNotifications(eq(user), isNull(), isNull(), any());
    }

    @Test
    public void testMarkAsRead_Success() {
        // Arrange
        Long id = 1L;
        com.smarthome.nexus.entity.User user = com.smarthome.nexus.entity.User.builder().id(1L).build();
        Notification notification = Notification.builder().id(id).user(user).isRead(false).build();
        
        when(notificationRepository.findById(id)).thenReturn(java.util.Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.markAsRead(id, user);

        // Assert
        verify(notificationRepository, times(1)).save(argThat(n -> n.getIsRead()));
    }

    @Test
    public void testMarkAllAsRead_Success() {
        // Arrange
        com.smarthome.nexus.entity.User user = new com.smarthome.nexus.entity.User();

        // Act
        notificationService.markAllAsRead(user);

        // Assert
        verify(notificationRepository, times(1)).markAllAsRead(user);
    }
}
