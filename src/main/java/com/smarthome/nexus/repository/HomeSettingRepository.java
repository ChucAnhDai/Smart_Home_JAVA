package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.HomeSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeSettingRepository extends JpaRepository<HomeSetting, Long> {
}
