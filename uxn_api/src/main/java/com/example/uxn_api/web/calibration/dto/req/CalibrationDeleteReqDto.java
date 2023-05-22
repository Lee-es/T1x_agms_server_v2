package com.example.uxn_api.web.calibration.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalibrationDeleteReqDto {

    @JsonProperty("user_id")
    private String userId;
}
