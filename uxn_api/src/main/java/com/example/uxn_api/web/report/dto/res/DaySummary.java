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
import java.time.LocalTime;
import java.util.ArrayList;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DaySummary {

    private LocalDate targetDate;
    private int dayOfWeek;

    private float averageGlucose;
    private long scanCount;
    private long lowerGlucoseCount;

    
   
}
