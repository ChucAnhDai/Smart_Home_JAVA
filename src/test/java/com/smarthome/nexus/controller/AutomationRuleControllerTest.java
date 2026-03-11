package com.smarthome.nexus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.dto.request.CreateAutomationRuleReq;
import com.smarthome.nexus.dto.request.UpdateAutomationRuleReq;
import com.smarthome.nexus.dto.response.AutomationRuleResDTO;
import com.smarthome.nexus.security.CustomUserDetailsService;
import com.smarthome.nexus.security.JwtTokenProvider;
import com.smarthome.nexus.service.AutomationRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutomationRuleController.class)
@AutoConfigureMockMvc
public class AutomationRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutomationRuleService automationRuleService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private AutomationRuleResDTO automationRuleResDTO;

    @BeforeEach
    void setUp() {
        automationRuleResDTO = new AutomationRuleResDTO();
        automationRuleResDTO.setId(1L);
        automationRuleResDTO.setName("Night Rule");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRule_success() throws Exception {
        CreateAutomationRuleReq req = new CreateAutomationRuleReq("Night Rule", "{}", "{}", true);
        
        when(automationRuleService.createRule(ArgumentMatchers.any())).thenReturn(automationRuleResDTO);

        mockMvc.perform(post("/api/automation-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Night Rule"));
    }

    @Test
    @WithMockUser
    void getRuleById_success() throws Exception {
        when(automationRuleService.getRuleById(1L)).thenReturn(automationRuleResDTO);

        mockMvc.perform(get("/api/automation-rules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRule_success() throws Exception {
        UpdateAutomationRuleReq req = new UpdateAutomationRuleReq("Updated Rule", null, null, null);
        
        when(automationRuleService.updateRule(ArgumentMatchers.eq(1L), ArgumentMatchers.any()))
                .thenReturn(automationRuleResDTO);

        mockMvc.perform(put("/api/automation-rules/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRule_success() throws Exception {
        mockMvc.perform(delete("/api/automation-rules/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRule_validationFailed() throws Exception {
        CreateAutomationRuleReq req = new CreateAutomationRuleReq("", "", "", null); // Invalid data

        mockMvc.perform(post("/api/automation-rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
