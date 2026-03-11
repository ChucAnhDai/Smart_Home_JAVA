package com.smarthome.nexus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "home_settings")
public class HomeSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_name", length = 100)
    private String homeName;

    @Column(length = 50)
    private String timezone;

    @Column(name = "dark_mode")
    private Boolean darkMode;

    @Column(name = "push_notif")
    private Boolean pushNotif;

    @PrePersist
    protected void onCreate() {
        if (homeName == null)
            homeName = "Nexus Smart Home";
        if (timezone == null)
            timezone = "Asia/Ho_Chi_Minh";
        if (darkMode == null)
            darkMode = false;
        if (pushNotif == null)
            pushNotif = true;
    }
}
