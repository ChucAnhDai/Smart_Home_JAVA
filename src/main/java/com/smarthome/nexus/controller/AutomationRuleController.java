package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.request.CreateAutomationRuleReq;
import com.smarthome.nexus.dto.request.UpdateAutomationRuleReq;
import com.smarthome.nexus.dto.response.AutomationRuleResDTO;
import com.smarthome.nexus.service.AutomationRuleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automation-rules")
@Slf4j
public class AutomationRuleController {

    private final AutomationRuleService automationRuleService;

    public AutomationRuleController(AutomationRuleService automationRuleService) {
        this.automationRuleService = automationRuleService;
    }

    @PostMapping
    public ResponseEntity<AutomationRuleResDTO> createRule(@Valid @RequestBody CreateAutomationRuleReq request) {
        log.info("REST request to create Automation Rule : {}", request);
        return new ResponseEntity<>(automationRuleService.createRule(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<AutomationRuleResDTO>> getAllRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        log.info("REST request to get all Automation Rules : page={}, size={}, search={}", page, size, search);
        return ResponseEntity.ok(automationRuleService.getAllRules(page, size, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutomationRuleResDTO> getRuleById(@PathVariable Long id) {
        log.info("REST request to get Automation Rule : {}", id);
        return ResponseEntity.ok(automationRuleService.getRuleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomationRuleResDTO> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAutomationRuleReq request) {
        log.info("REST request to update Automation Rule : {}, {}", id, request);
        return ResponseEntity.ok(automationRuleService.updateRule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        log.info("REST request to delete Automation Rule : {}", id);
        automationRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
