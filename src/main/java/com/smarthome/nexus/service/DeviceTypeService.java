package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateDeviceTypeReq;
import com.smarthome.nexus.dto.request.UpdateDeviceTypeReq;
import com.smarthome.nexus.dto.response.DeviceTypeResDTO;
import org.springframework.data.domain.Page;

public interface DeviceTypeService {
    DeviceTypeResDTO createDeviceType(CreateDeviceTypeReq request);

    DeviceTypeResDTO getDeviceTypeById(Long id);

    Page<DeviceTypeResDTO> getAllDeviceTypes(int page, int size, String sortBy, String sortDir, String search);

    DeviceTypeResDTO updateDeviceType(Long id, UpdateDeviceTypeReq request);

    void deleteDeviceType(Long id);
}
