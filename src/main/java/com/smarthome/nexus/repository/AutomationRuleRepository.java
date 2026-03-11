package com.smarthome.nexus.repository;

import com.smarthome.nexus.entity.AutomationRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutomationRuleRepository extends JpaRepository<AutomationRule, Long> {
    Page<AutomationRule> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<AutomationRule> findByIsActiveTrue();
}
