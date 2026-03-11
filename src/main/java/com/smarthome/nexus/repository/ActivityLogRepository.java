package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    @Query("SELECT a FROM ActivityLog a WHERE " +
            "(:deviceId IS NULL OR a.device.id = :deviceId) AND " +
            "(:startTime IS NULL OR a.createdAt >= :startTime) AND " +
            "(:endTime IS NULL OR a.createdAt <= :endTime)")
    Page<ActivityLog> filterLogs(
            @Param("deviceId") Long deviceId,
            @Param("startTime") Timestamp startTime,
            @Param("endTime") Timestamp endTime,
            Pageable pageable);
}
