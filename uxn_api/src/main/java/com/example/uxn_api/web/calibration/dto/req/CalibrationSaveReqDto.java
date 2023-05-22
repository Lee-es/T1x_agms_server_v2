package com.example.uxn_api.web.calibration.dto.req;

import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalibrationSaveReqDto {

    private String title;

    private String contents;

    private String createData;

    private Long userid;

}
