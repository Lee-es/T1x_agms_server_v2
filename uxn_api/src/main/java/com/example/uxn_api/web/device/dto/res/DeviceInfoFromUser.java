package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
public class DeviceInfoFromUser {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("diabetes_level")
    private Double diabetesLevel;

    @JsonProperty("user_id")
//    private User user;

    private UserInfoResDto userInfoResDto;

    public DeviceInfoFromUser(Device entity){
        this.deviceId = entity.getDeviceId();
        this.diabetesLevel = entity.getDiabetesLevel();
//        this.user = entity.getUser();
        this.userInfoResDto = new UserInfoResDto(entity.getUser());

    }





}
