package com.example.uxn_api.web.device.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.OptionalDouble;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloodSugarReportDto {

    private LocalDateTime startDay;
    private LocalDateTime endDay;
    private OptionalDouble average;
    private Double activatePercent;
    private int eventLowerCount;
    private String deviceId; // SN

}
