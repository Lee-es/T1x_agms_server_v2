package com.example.uxn_api.web.report.dto.res;

// import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
// import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_api.web.calibration.dto.res.CalibrationListResDto;
import com.example.uxn_api.web.note.dto.res.NoteListResDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter
@Setter

@AllArgsConstructor
@Builder
public class ReportDetailDto {

    // @JsonProperty("contents")

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private LocalDate startDate;
    private LocalDate endDate;

    private int dateRange;

    //1개의 데이터 = 1분
    private long wholeDataMinute;
    private long wholeDataCount;

    private long dataCount;
    private long dataMinute;

    //% 시간 CGM이 활성 상태임, 전체 분에 대한 데이터 존재 유무 체크
    private float sensorActivePercent;
    private String sensorActivePercentString;

    private float averageGlucose;
    private String averageGlucoseString;

    private long lowerGlucoseCount;

    private float gmi;
    private float gmiMol;

    private float glucoseCV;

    private long veryHighGlucoseCount;
    private long veryHighGlucoseMinute;
    private float veryHighGlucosePercent;

    private long highGlucoseCount;
    private long highGlucoseMinute;
    private float highGlucosePercent;

    private long targetGlucoseCount;
    private long targetGlucoseMinute;
    private float targetGlucosePercent;

    private long lowGlucoseCount;
    private long lowGlucoseMinute;
    private float lowGlucosePercent;

    private long veryLowGlucoseCount;
    private long veryLowGlucoseMinute;
    private float veryLowGlucosePercent;
    
    private Map<String, ArrayList> profileGraphData;

    private ArrayList<DayGraphDto> dayGraphDataList;
 
    private ImportantPattern importantPattern;

    private ArrayList<DeviceInformation> deviceInformations;







    public ReportDetailDto(){
        super();


    }
}
