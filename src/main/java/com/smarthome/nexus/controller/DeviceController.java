package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.request.CreateDeviceReq;
import com.smarthome.nexus.dto.request.UpdateDeviceReq;
import com.smarthome.nexus.dto.response.DeviceResDTO;
import com.smarthome.nexus.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<DeviceResDTO> createDevice(@Valid @RequestBody CreateDeviceReq request) {
        return new ResponseEntity<>(deviceService.createDevice(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<DeviceResDTO>> getAllDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(deviceService.getAllDevices(page, size, sortBy, sortDir, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResDTO> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Page<DeviceResDTO>> getDevicesByRoom(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(deviceService.getDevicesByRoom(roomId, page, size, sortBy, sortDir));
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<Page<DeviceResDTO>> getDevicesByType(
            @PathVariable Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return ResponseEntity.ok(deviceService.getDevicesByType(typeId, page, size, sortBy, sortDir));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResDTO> updateDevice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeviceReq request) {
        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @PatchMapping("/{id}/turn-on")
    public ResponseEntity<DeviceResDTO> turnOnDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.turnOnDevice(id));
    }

    @PatchMapping("/{id}/turn-off")
    public ResponseEntity<DeviceResDTO> turnOffDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.turnOffDevice(id));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<DeviceResDTO> toggleDevice(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.toggleDevice(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
