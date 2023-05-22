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
import java.time.LocalTime;
import java.util.ArrayList;


@Getter
@Setter

@AllArgsConstructor
@Builder
public class DayGraphDto {

    private LocalDate targetDate;
    private int dayOfWeek;

    
    private ArrayList<LocalDateTime> timeStringList;
    private ArrayList<Double> dataList;
    
   
    private DaySummary summary;

    private ArrayList<String>[] valueList = new ArrayList[12];
    private ArrayList<NoteListResDto> noteList = new ArrayList<>();
    private ArrayList<CalibrationListResDto> calibrationList = new ArrayList<>();

    public DayGraphDto() {
        for (int i = 0; i < 12; i++) {
            valueList[i] = new ArrayList<>();
        }
    }
}
