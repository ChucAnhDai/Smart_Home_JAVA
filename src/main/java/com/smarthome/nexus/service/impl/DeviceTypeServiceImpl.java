package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.CreateDeviceTypeReq;
import com.smarthome.nexus.dto.request.UpdateDeviceTypeReq;
import com.smarthome.nexus.dto.response.DeviceTypeResDTO;
import com.smarthome.nexus.entity.DeviceType;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceTypeRepository;
import com.smarthome.nexus.service.DeviceTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeRepository deviceTypeRepository;
    private final ModelMapper modelMapper;

    public DeviceTypeServiceImpl(DeviceTypeRepository deviceTypeRepository, ModelMapper modelMapper) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public DeviceTypeResDTO createDeviceType(CreateDeviceTypeReq request) {
        log.info("Creating new device type with name: {}", request.getName());
        DeviceType deviceType = modelMapper.map(request, DeviceType.class);
        DeviceType savedDeviceType = deviceTypeRepository.save(deviceType);
        log.info("Successfully created device type with id: {}", savedDeviceType.getId());
        return modelMapper.map(savedDeviceType, DeviceTypeResDTO.class);
    }

    @Override
    public DeviceTypeResDTO getDeviceTypeById(Long id) {
        DeviceType deviceType = deviceTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device type not found with id: " + id));
        return modelMapper.map(deviceType, DeviceTypeResDTO.class);
    }

    @Override
    public Page<DeviceTypeResDTO> getAllDeviceTypes(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeviceType> deviceTypes;
        if (search != null && !search.isEmpty()) {
            deviceTypes = deviceTypeRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            deviceTypes = deviceTypeRepository.findAll(pageable);
        }

        return deviceTypes.map(type -> modelMapper.map(type, DeviceTypeResDTO.class));
    }

    @Override
    @Transactional
    public DeviceTypeResDTO updateDeviceType(Long id, UpdateDeviceTypeReq request) {
        log.info("Updating device type with id: {}", id);
        DeviceType deviceType = deviceTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device type not found with id: {}", id);
                    return new ResourceNotFoundException("Device type not found with id: " + id);
                });

        deviceType.setName(request.getName());
        deviceType.setIcon(request.getIcon());
        deviceType.setDescription(request.getDescription());

        DeviceType updatedDeviceType = deviceTypeRepository.save(deviceType);
        log.info("Successfully updated device type with id: {}", id);
        return modelMapper.map(updatedDeviceType, DeviceTypeResDTO.class);
    }

    @Override
    @Transactional
    public void deleteDeviceType(Long id) {
        log.info("Deleting device type with id: {}", id);
        DeviceType deviceType = deviceTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Device type not found with id: {}", id);
                    return new ResourceNotFoundException("Device type not found with id: " + id);
                });
        deviceTypeRepository.delete(deviceType);
        log.info("Successfully deleted device type with id: {}", id);
    }
}
