package com.smarthome.nexus.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceTypeReq {
    @NotBlank(message = "Tên loại thiết bị không được để trống")
    @Size(max = 255, message = "Tên loại thiết bị không được vượt quá 255 ký tự")
    private String name;

    @NotBlank(message = "Icon không được để trống")
    private String icon;

    private String description;
}
