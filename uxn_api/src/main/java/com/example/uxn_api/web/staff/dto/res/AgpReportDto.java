package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_api.web.device.dto.res.DeviceForAgpReportDto;
import com.example.uxn_common.global.domain.device.Device;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgpReportDto {

    @JsonProperty("user_name")
    private String userName;

    // 일일 혈당수치 데이터
    @JsonProperty("device_info_list")
    private List<DeviceForAgpReportDto> deviceInfoList;

    // 백분율 데이터
    @JsonProperty("five_percent")
    private int fivePercent;

    @JsonProperty("twenty_five_percent")
    private int twentyFivePercent;

    @JsonProperty("fifty_percent")
    private int fiftyPercent;

    @JsonProperty("seventy_five_percent")
    private int seventyFivePercent;

    @JsonProperty("ninety_five_percent")
    private int ninetyFivePercent;

}
