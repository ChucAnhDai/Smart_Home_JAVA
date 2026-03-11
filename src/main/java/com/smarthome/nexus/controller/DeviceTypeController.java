package com.smarthome.nexus.controller;

import jakarta.validation.Valid;
import com.smarthome.nexus.dto.request.CreateDeviceTypeReq;
import com.smarthome.nexus.dto.request.UpdateDeviceTypeReq;
import com.smarthome.nexus.dto.response.DeviceTypeResDTO;
import com.smarthome.nexus.service.DeviceTypeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device-types")
public class DeviceTypeController {

    private final DeviceTypeService deviceTypeService;

    public DeviceTypeController(DeviceTypeService deviceTypeService) {
        this.deviceTypeService = deviceTypeService;
    }

    @PostMapping
    public ResponseEntity<DeviceTypeResDTO> createDeviceType(@Valid @RequestBody CreateDeviceTypeReq request) {
        return new ResponseEntity<>(deviceTypeService.createDeviceType(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<DeviceTypeResDTO>> getAllDeviceTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(deviceTypeService.getAllDeviceTypes(page, size, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceTypeResDTO> getDeviceTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceTypeService.getDeviceTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceTypeResDTO> updateDeviceType(@PathVariable Long id,
            @Valid @RequestBody UpdateDeviceTypeReq request) {
        return ResponseEntity.ok(deviceTypeService.updateDeviceType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceType(@PathVariable Long id) {
        deviceTypeService.deleteDeviceType(id);
        return ResponseEntity.noContent().build();
    }
}
