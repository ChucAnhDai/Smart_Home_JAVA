package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.CreateAutomationRuleReq;
import com.smarthome.nexus.dto.request.UpdateAutomationRuleReq;
import com.smarthome.nexus.dto.response.AutomationRuleResDTO;
import com.smarthome.nexus.entity.AutomationRule;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.AutomationRuleRepository;
import com.smarthome.nexus.service.AutomationRuleService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AutomationRuleServiceImpl implements AutomationRuleService {

    private final AutomationRuleRepository automationRuleRepository;
    private final ModelMapper modelMapper;

    public AutomationRuleServiceImpl(AutomationRuleRepository automationRuleRepository, ModelMapper modelMapper) {
        this.automationRuleRepository = automationRuleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AutomationRuleResDTO createRule(CreateAutomationRuleReq request) {
        log.info("Creating new Automation Rule: {}", request.getName());
        AutomationRule rule = modelMapper.map(request, AutomationRule.class);

        AutomationRule savedRule = automationRuleRepository.save(rule);
        log.info("Successfully created Automation Rule with ID: {}", savedRule.getId());
        return modelMapper.map(savedRule, AutomationRuleResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AutomationRuleResDTO getRuleById(Long id) {
        log.debug("Fetching Automation Rule with ID: {}", id);
        AutomationRule rule = automationRuleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation Rule not found with ID: {}", id);
                    return new ResourceNotFoundException("Automation Rule not found with id: " + id);
                });
        return modelMapper.map(rule, AutomationRuleResDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutomationRuleResDTO> getAllRules(int page, int size, String sortBy, String sortDir, String search) {
        log.debug("Fetching all Automation Rules (page={}, size={}, search={})", page, size, search);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AutomationRule> rules;
        if (search != null && !search.isEmpty()) {
            rules = automationRuleRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            rules = automationRuleRepository.findAll(pageable);
        }

        return rules.map(rule -> modelMapper.map(rule, AutomationRuleResDTO.class));
    }

    @Override
    @Transactional
    public AutomationRuleResDTO updateRule(Long id, UpdateAutomationRuleReq request) {
        log.info("Updating Automation Rule with ID: {}", id);
        AutomationRule rule = automationRuleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation Rule update failed - ID not found: {}", id);
                    return new ResourceNotFoundException("Automation Rule not found with id: " + id);
                });

        if (request.getName() != null)
            rule.setName(request.getName());
        if (request.getConditionText() != null)
            rule.setConditionText(request.getConditionText());
        if (request.getActionText() != null)
            rule.setActionText(request.getActionText());
        if (request.getIsActive() != null)
            rule.setIsActive(request.getIsActive());

        AutomationRule updatedRule = automationRuleRepository.save(rule);
        log.info("Successfully updated Automation Rule ID: {}", id);
        return modelMapper.map(updatedRule, AutomationRuleResDTO.class);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        log.info("Deleting Automation Rule with ID: {}", id);
        AutomationRule rule = automationRuleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Automation Rule deletion failed - ID not found: {}", id);
                    return new ResourceNotFoundException("Automation Rule not found with id: " + id);
                });
        automationRuleRepository.delete(rule);
        log.info("Successfully deleted Automation Rule ID: {}", id);
    }
}
