package com.smarthome.nexus.service;

import com.smarthome.nexus.dto.request.UpdateHomeSettingReq;
import com.smarthome.nexus.dto.response.HomeSettingResDTO;

public interface HomeSettingService {
    HomeSettingResDTO getHomeSetting();

    HomeSettingResDTO updateHomeSetting(UpdateHomeSettingReq request);
}
