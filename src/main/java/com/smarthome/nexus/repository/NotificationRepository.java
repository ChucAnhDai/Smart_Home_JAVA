package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.Notification;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE (n.user = :user OR n.user IS NULL) " +
            "AND (:type IS NULL OR n.type = :type) " +
            "AND (:isRead IS NULL OR n.isRead = :isRead)")
    Page<Notification> filterNotifications(
            @Param("user") User user,
            @Param("type") NotificationType type,
            @Param("isRead") Boolean isRead,
            Pageable pageable);

    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE (n.user = :user OR n.user IS NULL) AND n.isRead = false")
    void markAllAsRead(@Param("user") User user);
}
