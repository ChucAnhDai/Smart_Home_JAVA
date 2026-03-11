package com.smarthome.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomReq {
    @NotBlank(message = "Room name is required")
    @Size(max = 50, message = "Room name must not exceed 50 characters")
    private String name;

    @Size(max = 10, message = "Icon length must not exceed 10 characters")
    private String icon;
}
