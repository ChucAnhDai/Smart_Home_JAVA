package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateAutomationRuleReq;
import com.smarthome.nexus.dto.request.UpdateAutomationRuleReq;
import com.smarthome.nexus.dto.response.AutomationRuleResDTO;
import com.smarthome.nexus.entity.AutomationRule;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.AutomationRuleRepository;
import com.smarthome.nexus.service.impl.AutomationRuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutomationRuleServiceTest {

    @Mock
    private AutomationRuleRepository automationRuleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AutomationRuleServiceImpl automationRuleService;

    private AutomationRule automationRule;

    @BeforeEach
    void setUp() {
        automationRule = AutomationRule.builder()
                .id(1L)
                .name("Night Light")
                .conditionText("{\"time\": \"20:00\"}")
                .actionText("{\"device\": 1, \"state\": \"ON\"}")
                .isActive(true)
                .build();
    }

    @Test
    void createRule_success() {
        CreateAutomationRuleReq req = new CreateAutomationRuleReq("Night Light", "{\"time\": \"20:00\"}", "{\"device\": 1, \"state\": \"ON\"}", true);
        
        when(modelMapper.map(req, AutomationRule.class)).thenReturn(automationRule);
        when(automationRuleRepository.save(any(AutomationRule.class))).thenReturn(automationRule);
        when(modelMapper.map(automationRule, AutomationRuleResDTO.class)).thenReturn(new AutomationRuleResDTO());

        AutomationRuleResDTO result = automationRuleService.createRule(req);

        assertNotNull(result);
        verify(automationRuleRepository).save(any(AutomationRule.class));
    }

    @Test
    void getRuleById_success() {
        when(automationRuleRepository.findById(1L)).thenReturn(Optional.of(automationRule));
        when(modelMapper.map(automationRule, AutomationRuleResDTO.class)).thenReturn(new AutomationRuleResDTO());

        AutomationRuleResDTO result = automationRuleService.getRuleById(1L);

        assertNotNull(result);
        verify(automationRuleRepository).findById(1L);
    }

    @Test
    void getRuleById_notFound() {
        when(automationRuleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> automationRuleService.getRuleById(1L));
    }

    @Test
    void updateRule_success() {
        UpdateAutomationRuleReq req = new UpdateAutomationRuleReq("Morning Light", null, null, false);
        
        when(automationRuleRepository.findById(1L)).thenReturn(Optional.of(automationRule));
        when(automationRuleRepository.save(any(AutomationRule.class))).thenReturn(automationRule);
        when(modelMapper.map(automationRule, AutomationRuleResDTO.class)).thenReturn(new AutomationRuleResDTO());

        AutomationRuleResDTO result = automationRuleService.updateRule(1L, req);

        assertNotNull(result);
        assertEquals("Morning Light", automationRule.getName());
        assertFalse(automationRule.getIsActive());
        verify(automationRuleRepository).save(automationRule);
    }

    @Test
    void deleteRule_success() {
        when(automationRuleRepository.findById(1L)).thenReturn(Optional.of(automationRule));

        automationRuleService.deleteRule(1L);

        verify(automationRuleRepository).delete(automationRule);
    }
}
