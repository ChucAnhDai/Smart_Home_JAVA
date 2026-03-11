package com.smarthome.nexus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.dto.request.CreateDeviceReq;
import com.smarthome.nexus.dto.request.UpdateDeviceReq;
import com.smarthome.nexus.dto.response.DeviceResDTO;
import com.smarthome.nexus.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDevices_returnsPage() throws Exception {
        DeviceResDTO dto = DeviceResDTO.builder()
                .id(1L)
                .name("Smart Bulb")
                .status("OFF")
                .build();
        Page<DeviceResDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(deviceService.getAllDevices(anyInt(), anyInt(), anyString(), anyString(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/devices")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Smart Bulb"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDevice_returnsStatusCreated() throws Exception {
        CreateDeviceReq req = new CreateDeviceReq("New Device", 1L, 1L);
        DeviceResDTO res = DeviceResDTO.builder()
                .id(1L)
                .name("New Device")
                .build();

        when(deviceService.createDevice(any(CreateDeviceReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Device"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDevice_invalidData_returnsBadRequest() throws Exception {
        CreateDeviceReq req = new CreateDeviceReq("", null, null); // Invalid

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDeviceById_returnsDevice() throws Exception {
        DeviceResDTO res = DeviceResDTO.builder()
                .id(1L)
                .name("CCTV")
                .build();

        when(deviceService.getDeviceById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("CCTV"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDevice_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/devices/1"))
                .andExpect(status().isNoContent());
    }
}
