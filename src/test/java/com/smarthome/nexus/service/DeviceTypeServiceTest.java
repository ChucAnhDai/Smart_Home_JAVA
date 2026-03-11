package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.CreateDeviceTypeReq;
import com.smarthome.nexus.dto.request.UpdateDeviceTypeReq;
import com.smarthome.nexus.dto.response.DeviceTypeResDTO;
import com.smarthome.nexus.entity.DeviceType;
import com.smarthome.nexus.exception.ResourceNotFoundException;
import com.smarthome.nexus.repository.DeviceTypeRepository;
import com.smarthome.nexus.service.impl.DeviceTypeServiceImpl;
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
public class DeviceTypeServiceTest {

    @Mock
    private DeviceTypeRepository deviceTypeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DeviceTypeServiceImpl deviceTypeService;

    private DeviceType deviceType;
    private DeviceTypeResDTO deviceTypeResDTO;

    @BeforeEach
    void setUp() {
        deviceType = new DeviceType();
        deviceType.setId(1L);
        deviceType.setName("Light");
        deviceType.setIcon("bulb");

        deviceTypeResDTO = new DeviceTypeResDTO();
        deviceTypeResDTO.setId(1L);
        deviceTypeResDTO.setName("Light");
        deviceTypeResDTO.setIcon("bulb");
    }

    @Test
    void createDeviceType_Success() {
        CreateDeviceTypeReq req = new CreateDeviceTypeReq("Light", "bulb", "Desc");
        when(modelMapper.map(req, DeviceType.class)).thenReturn(deviceType);
        when(deviceTypeRepository.save(any(DeviceType.class))).thenReturn(deviceType);
        when(modelMapper.map(deviceType, DeviceTypeResDTO.class)).thenReturn(deviceTypeResDTO);

        DeviceTypeResDTO result = deviceTypeService.createDeviceType(req);

        assertNotNull(result);
        assertEquals("Light", result.getName());
        verify(deviceTypeRepository, times(1)).save(any(DeviceType.class));
    }

    @Test
    void getDeviceTypeById_Success() {
        when(deviceTypeRepository.findById(1L)).thenReturn(Optional.of(deviceType));
        when(modelMapper.map(deviceType, DeviceTypeResDTO.class)).thenReturn(deviceTypeResDTO);

        DeviceTypeResDTO result = deviceTypeService.getDeviceTypeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getDeviceTypeById_NotFound() {
        when(deviceTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> deviceTypeService.getDeviceTypeById(1L));
    }

    @Test
    void updateDeviceType_Success() {
        UpdateDeviceTypeReq req = new UpdateDeviceTypeReq("Updated Light", "bulb-on", "Updated description");
        when(deviceTypeRepository.findById(1L)).thenReturn(Optional.of(deviceType));
        when(deviceTypeRepository.save(any(DeviceType.class))).thenReturn(deviceType);
        when(modelMapper.map(any(DeviceType.class), eq(DeviceTypeResDTO.class))).thenReturn(deviceTypeResDTO);

        DeviceTypeResDTO result = deviceTypeService.updateDeviceType(1L, req);

        assertNotNull(result);
        verify(deviceTypeRepository).save(deviceType);
    }

    @Test
    void deleteDeviceType_Success() {
        when(deviceTypeRepository.findById(1L)).thenReturn(Optional.of(deviceType));
        doNothing().when(deviceTypeRepository).delete(deviceType);

        assertDoesNotThrow(() -> deviceTypeService.deleteDeviceType(1L));
        verify(deviceTypeRepository).delete(deviceType);
    }

    @Test
    void getAllDeviceTypes_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<DeviceType> page = new PageImpl<>(Collections.singletonList(deviceType));
        
        when(deviceTypeRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(DeviceType.class), eq(DeviceTypeResDTO.class))).thenReturn(deviceTypeResDTO);

        Page<DeviceTypeResDTO> result = deviceTypeService.getAllDeviceTypes(0, 10, "id", "asc", null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
