package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
    Page<DeviceType> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
