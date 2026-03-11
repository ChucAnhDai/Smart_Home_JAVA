package com.smarthome.nexus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHomeSettingReq {
    private String homeName;
    private String timezone;
    private Boolean darkMode;
    private Boolean pushNotif;
}
