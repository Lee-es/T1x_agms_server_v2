package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CheckDeviceResponse {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("diabetes_level")
    private Double diabetesLevel;

    @JsonProperty("create_data_time")
    private LocalDateTime createDataTime;

    public CheckDeviceResponse(Device entity){
        this.deviceId = entity.getDeviceId();
        this.diabetesLevel = entity.getDiabetesLevel();
        this.createDataTime = entity.getCreateDataTime();
    }

}
