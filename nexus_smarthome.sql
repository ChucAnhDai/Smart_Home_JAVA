-- Tạo cơ sở dữ liệu cho hệ thống Nexus Smart Home
CREATE DATABASE IF NOT EXISTS nexus_smarthome 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE nexus_smarthome;

-- ==========================================
-- 1. Bảng users
-- ==========================================
CREATE TABLE `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'MEMBER', 'GUEST') DEFAULT 'MEMBER',
    `status` ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    `last_active` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. Bảng rooms
-- ==========================================
CREATE TABLE `rooms` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `icon` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 3. Bảng device_types
-- ==========================================
CREATE TABLE `device_types` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `icon` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);

-- ==========================================
-- 4. Bảng devices
-- ==========================================
CREATE TABLE `devices` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `room_id` BIGINT,
    `type_id` BIGINT,
    `status` ENUM('ON', 'OFF') DEFAULT 'OFF',
    `is_online` BOOLEAN DEFAULT FALSE,
    `energy_kw` FLOAT DEFAULT 0.0,
    `last_active_time` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`room_id`) REFERENCES `rooms`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`type_id`) REFERENCES `device_types`(`id`) ON DELETE SET NULL
);

-- ==========================================
-- 5. Bảng activity_logs
-- ==========================================
CREATE TABLE `activity_logs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `device_id` BIGINT,
    `event_type` VARCHAR(50) NOT NULL,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`device_id`) REFERENCES `devices`(`id`) ON DELETE CASCADE
);

-- ==========================================
-- 6. Bảng automation_rules
-- ==========================================
CREATE TABLE `automation_rules` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `condition_text` JSON NOT NULL,
    `action_text` JSON NOT NULL,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 7. Bảng notifications
-- ==========================================
CREATE TABLE `notifications` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `message` TEXT NOT NULL,
    `type` ENUM('INFO', 'WARNING', 'CRITICAL') DEFAULT 'INFO',
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 8. Bảng home_settings
-- ==========================================
CREATE TABLE `home_settings` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `home_name` VARCHAR(100) NOT NULL,
    `timezone` VARCHAR(50) DEFAULT 'UTC',
    `dark_mode` BOOLEAN DEFAULT FALSE,
    `push_notif` BOOLEAN DEFAULT TRUE
);

-- ==========================================
-- 9. Bảng energy_monitoring
-- ==========================================
CREATE TABLE `energy_monitoring` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `device_id` BIGINT,
    `energy_kw` FLOAT NOT NULL,
    `recorded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`device_id`) REFERENCES `devices`(`id`) ON DELETE CASCADE
);
