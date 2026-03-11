package com.smarthome.nexus.config;

import com.smarthome.nexus.entity.Role;
import com.smarthome.nexus.entity.User;
import com.smarthome.nexus.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Tạo tài khoản admin nếu chưa tồn tại
        String adminEmail = "admin@gmail.com";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> {
                    // Nếu tài khoản đã tồn tại nhưng chưa phải ADMIN, nâng cấp lên
                    if (user.getRole() != Role.ADMIN) {
                        user.setRole(Role.ADMIN);
                        userRepository.save(user);
                        log.info(">>> Upgraded user '{}' to ADMIN role", adminEmail);
                    } else {
                        log.info(">>> Admin user '{}' already exists with ADMIN role", adminEmail);
                    }
                },
                () -> {
                    // Nếu chưa tồn tại, tạo mới
                    User admin = User.builder()
                            .fullName("Admin")
                            .email(adminEmail)
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .status("ACTIVE")
                            .build();
                    userRepository.save(admin);
                    log.info(">>> Created new ADMIN user: {}", adminEmail);
                }
        );
    }
}
