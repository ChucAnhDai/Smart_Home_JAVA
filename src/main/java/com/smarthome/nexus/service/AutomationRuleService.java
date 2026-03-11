package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateAutomationRuleReq;
import com.smarthome.nexus.dto.request.UpdateAutomationRuleReq;
import com.smarthome.nexus.dto.response.AutomationRuleResDTO;
import org.springframework.data.domain.Page;

public interface AutomationRuleService {
    AutomationRuleResDTO createRule(CreateAutomationRuleReq request);

    AutomationRuleResDTO getRuleById(Long id);

    Page<AutomationRuleResDTO> getAllRules(int page, int size, String sortBy, String sortDir, String search);

    AutomationRuleResDTO updateRule(Long id, UpdateAutomationRuleReq request);

    void deleteRule(Long id);
}
