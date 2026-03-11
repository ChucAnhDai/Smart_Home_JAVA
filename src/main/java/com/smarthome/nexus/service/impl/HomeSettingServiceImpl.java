package com.smarthome.nexus.service.impl;

import com.smarthome.nexus.dto.request.UpdateHomeSettingReq;
import com.smarthome.nexus.dto.response.HomeSettingResDTO;
import com.smarthome.nexus.entity.HomeSetting;
import com.smarthome.nexus.repository.HomeSettingRepository;
import com.smarthome.nexus.service.HomeSettingService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeSettingServiceImpl implements HomeSettingService {

    private final HomeSettingRepository homeSettingRepository;
    private final ModelMapper modelMapper;

    public HomeSettingServiceImpl(HomeSettingRepository homeSettingRepository, ModelMapper modelMapper) {
        this.homeSettingRepository = homeSettingRepository;
        this.modelMapper = modelMapper;
    }

    private HomeSetting getOrCreateDefaultSetting() {
        List<HomeSetting> settings = homeSettingRepository.findAll();
        if (settings.isEmpty()) {
            HomeSetting defaultSetting = HomeSetting.builder()
                    .homeName("Nexus Smart Home")
                    .timezone("Asia/Ho_Chi_Minh")
                    .darkMode(false)
                    .pushNotif(true)
                    .build();
            return homeSettingRepository.save(defaultSetting);
        }
        return settings.get(0); // Luôn lấy record đầu tiên làm cấu hình duy nhất của nhà
    }

    @Override
    public HomeSettingResDTO getHomeSetting() {
        HomeSetting setting = getOrCreateDefaultSetting();
        return modelMapper.map(setting, HomeSettingResDTO.class);
    }

    @Override
    public HomeSettingResDTO updateHomeSetting(UpdateHomeSettingReq request) {
        HomeSetting setting = getOrCreateDefaultSetting();

        if (request.getHomeName() != null)
            setting.setHomeName(request.getHomeName());
        if (request.getTimezone() != null)
            setting.setTimezone(request.getTimezone());
        if (request.getDarkMode() != null)
            setting.setDarkMode(request.getDarkMode());
        if (request.getPushNotif() != null)
            setting.setPushNotif(request.getPushNotif());

        HomeSetting updatedSetting = homeSettingRepository.save(setting);
        return modelMapper.map(updatedSetting, HomeSettingResDTO.class);
    }
}
