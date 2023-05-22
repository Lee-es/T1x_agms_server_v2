package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_api.web.user.dto.res.UserInfoToDiabetes;
import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("diabetes_level")
    private Double diabetesLevel;

    private final UserInfoToDiabetes userInfoToDiabetes;

    public UserInfoResponse(Device entity){
        this.deviceId = entity.getDeviceId();
        this.diabetesLevel = entity.getDiabetesLevel();
        this.userInfoToDiabetes = new UserInfoToDiabetes(entity.getUser());
    }


}
