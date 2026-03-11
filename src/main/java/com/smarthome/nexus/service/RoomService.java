package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateRoomReq;
import com.smarthome.nexus.dto.request.UpdateRoomReq;
import com.smarthome.nexus.dto.response.RoomResDTO;
import org.springframework.data.domain.Page;

public interface RoomService {
    RoomResDTO createRoom(CreateRoomReq request);

    RoomResDTO getRoomById(Long id);

    Page<RoomResDTO> getAllRooms(int page, int size, String sortBy, String sortDir, String search);

    RoomResDTO updateRoom(Long id, UpdateRoomReq request);

    void deleteRoom(Long id);
}
