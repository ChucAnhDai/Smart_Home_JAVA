package com.smarthome.nexus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAutomationRuleReq {
    private String name;
    private String conditionText;
    private String actionText;
    private Boolean isActive;
}
