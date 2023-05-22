package com.example.uxn_api.web.report.dto.res;

// import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
// import com.example.uxn_common.global.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportItemDto {

    // @JsonProperty("contents")

    
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private LocalDate startDate;
    private LocalDate endDate;

    private double minGlucose;
    private double maxGlucose;

    private double averageGlucose;
    private String averageGlucoseString;

    private String deviceID;

     //1개의 데이터 = 1분
    private long wholeDataMinute;
    private long wholeDataCount;

    private long dataCount;
    private long dataMinute;

    //% 시간 CGM이 활성 상태임, 전체 분에 대한 데이터 존재 유무 체크
    private float sensorActivePercent;
    private String sensorActivePercentString;

    private long lowerGlucoseCount;
    
    private Map<String, ArrayList> profileGraphData;

    
}
