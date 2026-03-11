package com.smarthome.nexus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.dto.request.CreateUserReq;
import com.smarthome.nexus.dto.response.UserResDTO;
import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_AsAdmin_Success() throws Exception {
        CreateUserReq req = new CreateUserReq("John Doe", "john@example.com", "password123", Role.MEMBER, "ACTIVE");
        UserResDTO res = UserResDTO.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@example.com")
                .role(Role.MEMBER)
                .status("ACTIVE")
                .build();

        when(userService.createUser(any(CreateUserReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getAllUsers_AsMember_Forbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_AsAdmin_Success() throws Exception {
        UserResDTO res = UserResDTO.builder().id(1L).email("john@example.com").build();
        when(userService.getUserById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
