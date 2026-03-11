package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Page<Device> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    java.util.List<Device> findByStatusIgnoreCase(String status);

    Page<Device> findByRoomId(Long roomId, Pageable pageable);

    Page<Device> findByDeviceTypeId(Long typeId, Pageable pageable);

    long countByStatusIgnoreCase(String status);

    long countByIsOnline(Boolean isOnline);

    long countByRoomId(Long roomId);

    long countByRoomIdAndStatusIgnoreCase(Long roomId, String status);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(d.energyKw) FROM Device d WHERE d.room.id = :roomId AND UPPER(d.status) = 'ON'")
    Double sumEnergyByRoomId(Long roomId);

    @org.springframework.data.jpa.repository.Query("SELECT d.room.id as roomId, COUNT(d) as total, " +
            "SUM(CASE WHEN UPPER(d.status) = 'ON' THEN 1 ELSE 0 END) as active, " +
            "SUM(CASE WHEN UPPER(d.status) = 'ON' THEN d.energyKw ELSE 0 END) as energy " +
            "FROM Device d GROUP BY d.room.id")
    java.util.List<RoomStats> getRoomStats();

    interface RoomStats {
        Long getRoomId();

        Long getTotal();

        Long getActive();

        Double getEnergy();
    }

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Device d SET d.room = null WHERE d.room.id = :roomId")
    void unassignDevicesFromRoom(Long roomId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE devices SET room_id = NULL WHERE room_id NOT IN (SELECT id FROM rooms)", nativeQuery = true)
    void fixOrphanDevices();
}
