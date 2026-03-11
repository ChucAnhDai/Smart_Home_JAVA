package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateRoomReq;
import com.smarthome.nexus.dto.request.UpdateRoomReq;
import com.smarthome.nexus.dto.response.RoomResDTO;
import com.smarthome.nexus.entity.Room;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.RoomRepository;
import com.smarthome.nexus.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;
    private RoomResDTO roomResDTO;

    @BeforeEach
    void setUp() {
        room = Room.builder()
                .id(1L)
                .name("Living Room")
                .icon("🛋️")
                .build();

        roomResDTO = new RoomResDTO();
        roomResDTO.setId(1L);
        roomResDTO.setName("Living Room");
        roomResDTO.setIcon("🛋️");
    }

    @Test
    void createRoom_Success() {
        CreateRoomReq req = new CreateRoomReq("Living Room", "🛋️");
        when(modelMapper.map(req, Room.class)).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(modelMapper.map(room, RoomResDTO.class)).thenReturn(roomResDTO);

        RoomResDTO result = roomService.createRoom(req);

        assertNotNull(result);
        assertEquals("Living Room", result.getName());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void getRoomById_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(modelMapper.map(room, RoomResDTO.class)).thenReturn(roomResDTO);

        RoomResDTO result = roomService.getRoomById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getRoomById_NotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roomService.getRoomById(1L));
    }

    @Test
    void getAllRooms_Success() {
        Page<Room> roomPage = new PageImpl<>(Collections.singletonList(room));

        when(roomRepository.findAll(any(Pageable.class))).thenReturn(roomPage);
        when(modelMapper.map(room, RoomResDTO.class)).thenReturn(roomResDTO);

        Page<RoomResDTO> result = roomService.getAllRooms(0, 10, "id", "asc", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateRoom_Success() {
        UpdateRoomReq req = new UpdateRoomReq("New Name", "🏠");
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(modelMapper.map(room, RoomResDTO.class)).thenReturn(roomResDTO);

        RoomResDTO result = roomService.updateRoom(1L, req);

        assertNotNull(result);
        verify(roomRepository).save(room);
    }

    @Test
    void deleteRoom_Success() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(deviceRepository).unassignDevicesFromRoom(1L);
        verify(roomRepository).delete(room);
    }
}
