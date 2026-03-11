package com.smarthome.nexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutomationRuleResDTO {
    private Long id;
    private String name;
    private String conditionText;
    private String actionText;
    private Boolean isActive;
    private Timestamp createdAt;
}
