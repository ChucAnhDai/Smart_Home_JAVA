package com.smarthome.nexus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthome.nexus.dto.request.CreateRoomReq;
import com.smarthome.nexus.dto.response.RoomResDTO;
import com.smarthome.nexus.service.RoomService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRooms_returnsPage() throws Exception {
        RoomResDTO dto = new RoomResDTO();
        dto.setId(1L);
        dto.setName("Living Room");
        Page<RoomResDTO> page = new PageImpl<>(Collections.singletonList(dto));

        when(roomService.getAllRooms(any(Integer.class), any(Integer.class), any(String.class), any(String.class),
                any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/rooms")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Living Room"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRoom_returnsStatusCreated() throws Exception {
        CreateRoomReq req = new CreateRoomReq("New Room", "🏠");
        RoomResDTO res = new RoomResDTO();
        res.setId(1L);
        res.setName("New Room");

        when(roomService.createRoom(any(CreateRoomReq.class))).thenReturn(res);

        mockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Room"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRoomById_returnsRoom() throws Exception {
        RoomResDTO res = new RoomResDTO();
        res.setId(1L);
        res.setName("Living Room");

        when(roomService.getRoomById(1L)).thenReturn(res);

        mockMvc.perform(get("/api/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Living Room"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoom_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/rooms/1"))
                .andExpect(status().isNoContent());
    }
}
