package com.smarthome.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAutomationRuleReq {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Condition text is required")
    @NotNull(message = "Condition text cannot be null")
    private String conditionText;

    @NotBlank(message = "Action text is required")
    @NotNull(message = "Action text cannot be null")
    private String actionText;

    @NotNull(message = "Status is required")
    private Boolean isActive;
}
