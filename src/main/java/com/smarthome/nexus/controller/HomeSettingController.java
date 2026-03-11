package com.smarthome.nexus.controller;

import com.smarthome.nexus.dto.request.UpdateHomeSettingReq;
import com.smarthome.nexus.dto.response.HomeSettingResDTO;
import com.smarthome.nexus.service.HomeSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home-settings")
public class HomeSettingController {

    private final HomeSettingService homeSettingService;

    public HomeSettingController(HomeSettingService homeSettingService) {
        this.homeSettingService = homeSettingService;
    }

    @GetMapping
    public ResponseEntity<HomeSettingResDTO> getHomeSetting() {
        return ResponseEntity.ok(homeSettingService.getHomeSetting());
    }

    @PutMapping
    public ResponseEntity<HomeSettingResDTO> updateHomeSetting(@RequestBody UpdateHomeSettingReq request) {
        return ResponseEntity.ok(homeSettingService.updateHomeSetting(request));
    }
}
