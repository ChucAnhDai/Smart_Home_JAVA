package com.smarthome.nexus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.dto.request.CreateDeviceTypeReq;
import com.smarthome.nexus.dto.response.DeviceTypeResDTO;
import com.smarthome.nexus.service.DeviceTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DeviceTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceTypeService deviceTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDeviceType_InvalidInput_ReturnsBadRequest() throws Exception {
        CreateDeviceTypeReq req = new CreateDeviceTypeReq("", "", ""); // Invalid input

        mockMvc.perform(post("/api/device-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDeviceType_ValidInput_ReturnsCreated() throws Exception {
        CreateDeviceTypeReq req = new CreateDeviceTypeReq("Light", "bulb", "Light description");
        DeviceTypeResDTO res = new DeviceTypeResDTO();
        res.setId(1L);
        res.setName("Light");
        res.setIcon("bulb");

        when(deviceTypeService.createDeviceType(any(CreateDeviceTypeReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/device-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Light"));
    }
}
