package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiabetesDto {

    private Double diabetes;

    @JsonProperty("create_data_time")
    private LocalDateTime createDataTime;

    public DiabetesDto(Device entity) {
        this.diabetes = entity.getDiabetesLevel();
        this.createDataTime = entity.getCreateDataTime();
    }
}
