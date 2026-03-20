package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.CreateRoomReq;
import com.smarthome.nexus.dto.request.UpdateRoomReq;
import com.smarthome.nexus.dto.response.RoomResDTO;
import com.smarthome.nexus.entity.Room;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.RoomRepository;
import com.smarthome.nexus.service.RoomService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;
    private final ModelMapper modelMapper;

    public RoomServiceImpl(RoomRepository roomRepository, DeviceRepository deviceRepository, ModelMapper modelMapper) {
        this.roomRepository = roomRepository;
        this.deviceRepository = deviceRepository;
        this.modelMapper = modelMapper;
    }

    private RoomResDTO mapToDTO(Room room) {
        RoomResDTO dto = modelMapper.map(room, RoomResDTO.class);
        dto.setTotalDevices((int) deviceRepository.countByRoomId(room.getId()));
        dto.setActiveDevices((int) deviceRepository.countByRoomIdAndStatusIgnoreCase(room.getId(), "ON"));
        Double energy = deviceRepository.sumEnergyByRoomId(room.getId());
        dto.setEnergyUsage(energy != null ? energy : 0.0);
        return dto;
    }

    private RoomResDTO mapToDTOWithStats(Room room, DeviceRepository.RoomStats stats) {
        RoomResDTO dto = modelMapper.map(room, RoomResDTO.class);
        if (stats != null) {
            dto.setTotalDevices(stats.getTotal().intValue());
            dto.setActiveDevices(stats.getActive().intValue());
            dto.setEnergyUsage(stats.getEnergy() != null ? stats.getEnergy() : 0.0);
        } else {
            dto.setTotalDevices(0);
            dto.setActiveDevices(0);
            dto.setEnergyUsage(0.0);
        }
        return dto;
    }

    @Override
    public RoomResDTO createRoom(CreateRoomReq request) {
        log.info("Creating new room with name: {}", request.getName());
        Room room = modelMapper.map(request, Room.class);
        Room savedRoom = roomRepository.save(room);
        return mapToDTO(savedRoom);
    }

    @Override
    public RoomResDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return mapToDTO(room);
    }

    @Override
    public Page<RoomResDTO> getAllRooms(int page, int size, String sortBy, String sortDir, String search) {
        log.debug("Fetching rooms - page: {}, size: {}, search: {}", page, size, search);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Room> rooms;
        if (search != null && !search.isEmpty()) {
            rooms = roomRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            rooms = roomRepository.findAll(pageable);
        }

        // Performance Optimization: Batch fetch stats for all rooms at once
        java.util.List<DeviceRepository.RoomStats> allStats = deviceRepository.getRoomStats();
        java.util.Map<Long, DeviceRepository.RoomStats> statsMap = allStats.stream()
                .filter(s -> s.getRoomId() != null)
                .collect(java.util.stream.Collectors.toMap(DeviceRepository.RoomStats::getRoomId, s -> s));

        return rooms.map(room -> mapToDTOWithStats(room, statsMap.get(room.getId())));
    }

    @Override
    public RoomResDTO updateRoom(Long id, UpdateRoomReq request) {
        log.info("Updating room with id: {}, new name: {}", id, request.getName());
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        room.setName(request.getName());
        room.setIcon(request.getIcon());

        Room updatedRoom = roomRepository.save(room);
        return mapToDTO(updatedRoom);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        log.warn("Deleting room with id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Room not found with id: {}", id);
                    return new ResourceNotFoundException("Room not found with id: " + id);
                });

        roomRepository.delete(room);
        log.info("Successfully deleted room with id: {}", id);
    }
}
