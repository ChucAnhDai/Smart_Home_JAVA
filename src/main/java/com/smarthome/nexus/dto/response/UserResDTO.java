package com.smarthome.nexus.dto.response;

import com.smarthome.nexus.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String status;
    private Timestamp lastActive;
    private Timestamp createdAt;
}
