package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class DeviceForAgpReportDto {

    @JsonProperty("date_time")
    private LocalDateTime dateTime;

    private Double diabetes;

    public DeviceForAgpReportDto (Device entity) {
        this.dateTime = entity.getCreateDataTime();
        this.diabetes = entity.getDiabetesLevel();

    }
}
