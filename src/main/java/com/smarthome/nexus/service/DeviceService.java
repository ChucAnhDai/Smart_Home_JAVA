package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateDeviceReq;
import com.smarthome.nexus.dto.request.UpdateDeviceReq;
import com.smarthome.nexus.dto.response.DeviceResDTO;
import org.springframework.data.domain.Page;

public interface DeviceService {
    DeviceResDTO createDevice(CreateDeviceReq request);

    DeviceResDTO getDeviceById(Long id);

    Page<DeviceResDTO> getAllDevices(int page, int size, String sortBy, String sortDir, String search);

    Page<DeviceResDTO> getDevicesByRoom(Long roomId, int page, int size, String sortBy, String sortDir);

    Page<DeviceResDTO> getDevicesByType(Long typeId, int page, int size, String sortBy, String sortDir);

    DeviceResDTO updateDevice(Long id, UpdateDeviceReq request);

    DeviceResDTO turnOnDevice(Long id);

    DeviceResDTO turnOffDevice(Long id);

    DeviceResDTO toggleDevice(Long id);

    DeviceResDTO changeDeviceStatus(Long id, String status);

    void deleteDevice(Long id);
}
