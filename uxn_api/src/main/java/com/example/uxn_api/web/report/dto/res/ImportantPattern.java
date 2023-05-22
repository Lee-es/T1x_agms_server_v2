package com.example.uxn_api.web.report.dto.res;

// import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
// import com.example.uxn_common.global.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportantPattern {

    private String kindKeyword;
    private String timeKeyword;
    private String medicine;
    private ArrayList<String> medicineQuestion;

    private String lifeStyle;
    private ArrayList<String> lifeStyleQuestion;

   
}
