package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.CreateDeviceReq;
import com.smarthome.nexus.dto.request.UpdateDeviceReq;
import com.smarthome.nexus.dto.response.DeviceResDTO;
import com.smarthome.nexus.entity.Device;
import com.smarthome.nexus.entity.DeviceType;
import com.smarthome.nexus.entity.Room;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceRepository;
import com.smarthome.nexus.repository.DeviceTypeRepository;
import com.smarthome.nexus.repository.RoomRepository;
import com.smarthome.nexus.service.DeviceService;
import org.modelmapper.ModelMapper;
import com.smarthome.nexus.event.DeviceStatusChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import com.smarthome.nexus.service.ActivityLogService;
import com.smarthome.nexus.service.NotificationService;
import com.smarthome.nexus.entity.NotificationType;
import com.smarthome.nexus.service.RealtimeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final RoomRepository roomRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final RealtimeService realtimeService;

    public DeviceServiceImpl(DeviceRepository deviceRepository,
            RoomRepository roomRepository,
            DeviceTypeRepository deviceTypeRepository,
            ModelMapper modelMapper,
            ApplicationEventPublisher eventPublisher,
            ActivityLogService activityLogService,
            NotificationService notificationService,
            RealtimeService realtimeService) {
        this.deviceRepository = deviceRepository;
        this.roomRepository = roomRepository;
        this.deviceTypeRepository = deviceTypeRepository;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
        this.realtimeService = realtimeService;
    }

    @Override
    public DeviceResDTO createDevice(CreateDeviceReq request) {
        Room room = null;
        if (request.getRoomId() != null) {
            room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }

        DeviceType type = null;
        if (request.getTypeId() != null) {
            type = deviceTypeRepository.findById(request.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeviceType not found"));
        }

        Device device = Device.builder()
                .name(request.getName())
                .room(room)
                .deviceType(type)
                .status("OFF")
                .isOnline(true)
                .energyKw(0.0f)
                .build();

        Device savedDevice = deviceRepository.save(device);
        return modelMapper.map(savedDevice, DeviceResDTO.class);
    }

    @Override
    public DeviceResDTO getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        return modelMapper.map(device, DeviceResDTO.class);
    }

    @Override
    public Page<DeviceResDTO> getAllDevices(int page, int size, String sortBy, String sortDir, String search) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Device> devices;

        if (search != null && !search.isEmpty()) {
            devices = deviceRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            devices = deviceRepository.findAll(pageable);
        }

        return devices.map(d -> modelMapper.map(d, DeviceResDTO.class));
    }

    @Override
    public Page<DeviceResDTO> getDevicesByRoom(Long roomId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Device> devices = deviceRepository.findByRoomId(roomId, pageable);
        return devices.map(d -> modelMapper.map(d, DeviceResDTO.class));
    }

    @Override
    public Page<DeviceResDTO> getDevicesByType(Long typeId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Device> devices = deviceRepository.findByDeviceTypeId(typeId, pageable);
        return devices.map(d -> modelMapper.map(d, DeviceResDTO.class));
    }

    @Override
    public DeviceResDTO updateDevice(Long id, UpdateDeviceReq request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            device.setRoom(room);
        }

        if (request.getTypeId() != null) {
            DeviceType type = deviceTypeRepository.findById(request.getTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeviceType not found"));
            device.setDeviceType(type);
        }

        device.setName(request.getName());
        if (request.getStatus() != null)
            device.setStatus(request.getStatus());
        if (request.getIsOnline() != null)
            device.setIsOnline(request.getIsOnline());
        if (request.getEnergyKw() != null)
            device.setEnergyKw(request.getEnergyKw());

        Device updatedDevice = deviceRepository.save(device);
        return modelMapper.map(updatedDevice, DeviceResDTO.class);
    }

    @Override
    public DeviceResDTO turnOnDevice(Long id) {
        return changeDeviceStatus(id, "ON");
    }

    @Override
    public DeviceResDTO turnOffDevice(Long id) {
        return changeDeviceStatus(id, "OFF");
    }

    @Override
    public DeviceResDTO toggleDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        String newStatus = "ON".equalsIgnoreCase(device.getStatus()) ? "OFF" : "ON";
        return changeDeviceStatus(id, newStatus);
    }

    @Override
    @Transactional
    public DeviceResDTO changeDeviceStatus(Long id, String status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        String oldStatus = device.getStatus();
        device.setStatus(status);
        device.setLastActiveTime(new Timestamp(System.currentTimeMillis()));

        // Demo logic: set proxy energy consumption if turning ON and currently 0.0
        if ("ON".equalsIgnoreCase(status) && (device.getEnergyKw() == null || device.getEnergyKw() <= 0.0f)) {
            device.setEnergyKw(getProxyEnergyByType(device.getDeviceType()));
        }

        Device updatedDevice = deviceRepository.save(device);

        // Publish event for automation rules
        eventPublisher.publishEvent(new DeviceStatusChangedEvent(this, id, status));

        // Log activity
        activityLogService.logActivity(id, "STATUS_" + status,
                "Device (ID:" + id + ") turned " + status + " via "
                        + (status.equalsIgnoreCase(oldStatus) ? "System" : "Interaction"));

        // Generate Notification
        notificationService.createGlobalNotification(
                "Device '" + updatedDevice.getName() + "' status changed to " + status.toUpperCase(),
                NotificationType.DEVICE);

        // Broadcast realtime update
        realtimeService.broadcastDeviceStatus(updatedDevice.getId(), updatedDevice.getName(), 
                updatedDevice.getStatus(), updatedDevice.getIsOnline());

        return modelMapper.map(updatedDevice, DeviceResDTO.class);
    }

    @Override
    @Transactional
    public void deleteDevice(Long id) {
        log.warn("Deleting device with id: {}", id);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device not found with id: {}", id);
                    return new ResourceNotFoundException("Device not found");
                });
        deviceRepository.delete(device);
        log.info("Successfully deleted device with id: {}", id);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private Float getProxyEnergyByType(DeviceType type) {
        if (type == null || type.getName() == null)
            return 0.1f;
        String name = type.getName().toLowerCase();
        if (name.contains("đèn") || name.contains("light"))
            return 0.05f;
        if (name.contains("quạt") || name.contains("fan"))
            return 0.1f;
        if (name.contains("tivi") || name.contains("tv"))
            return 0.2f;
        if (name.contains("điều hòa") || name.contains("air") || name.contains("conditioner"))
            return 1.5f;
        if (name.contains("tủ lạnh") || name.contains("fridge"))
            return 0.3f;
        return 0.1f;
    }
}
