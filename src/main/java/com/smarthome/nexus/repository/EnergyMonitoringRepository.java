package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.EnergyMonitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface EnergyMonitoringRepository extends JpaRepository<EnergyMonitoring, Long> {

    @Query("SELECT e FROM EnergyMonitoring e WHERE " +
            "(:deviceId IS NULL OR e.device.id = :deviceId) AND " +
            "(:startTime IS NULL OR e.recordedAt >= :startTime) AND " +
            "(:endTime IS NULL OR e.recordedAt <= :endTime)")
    Page<EnergyMonitoring> filterEnergyRecords(
            @Param("deviceId") Long deviceId,
            @Param("startTime") Timestamp startTime,
            @Param("endTime") Timestamp endTime,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.energyKwh), 0.0) FROM EnergyMonitoring e")
    Double calculateTotalEnergyConsumption();
}
