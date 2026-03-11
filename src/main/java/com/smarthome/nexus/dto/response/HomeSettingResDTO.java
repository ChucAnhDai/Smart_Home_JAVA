package com.smarthome.nexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeSettingResDTO {
    private Long id;
    private String homeName;
    private String timezone;
    private Boolean darkMode;
    private Boolean pushNotif;
}
