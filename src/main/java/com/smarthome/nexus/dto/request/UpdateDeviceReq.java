package com.smarthome.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceReq {
    @NotBlank(message = "Device name is required")
    private String name;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Device type ID is required")
    private Long typeId;

    private String status;
    private Boolean isOnline;
    private Float energyKw;
}
