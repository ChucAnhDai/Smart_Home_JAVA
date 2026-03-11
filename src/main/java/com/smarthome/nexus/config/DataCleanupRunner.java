package com.smarthome.nexus.config;

import com.smarthome.nexus.repository.DeviceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataCleanupRunner implements CommandLineRunner {

    private final DeviceRepository deviceRepository;

    public DataCleanupRunner(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        deviceRepository.fixOrphanDevices();
    }
}
