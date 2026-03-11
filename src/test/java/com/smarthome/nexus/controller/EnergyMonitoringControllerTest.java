package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.response.EnergyMonitoringResDTO;
import com.smarthome.nexus.service.EnergyMonitoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EnergyMonitoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnergyMonitoringService energyMonitoringService;

    @Test
    @WithMockUser(roles = "USER")
    void getEnergyRecords_returnsPage() throws Exception {
        EnergyMonitoringResDTO dto = EnergyMonitoringResDTO.builder()
                .id(1L)
                .deviceId(1L)
                .deviceName("Test Device")
                .energyKwh(2.5)
                .build();
        Page<EnergyMonitoringResDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(energyMonitoringService.getEnergyRecords(any(), any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        mockMvc.perform(get("/api/energy-monitoring")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceName").value("Test Device"))
                .andExpect(jsonPath("$.content[0].energyKwh").value(2.5));
    }

    @Test
    void getEnergyRecords_unauthorized_returns403() throws Exception {
        mockMvc.perform(get("/api/energy-monitoring"))
                .andExpect(status().isForbidden());
    }
}
