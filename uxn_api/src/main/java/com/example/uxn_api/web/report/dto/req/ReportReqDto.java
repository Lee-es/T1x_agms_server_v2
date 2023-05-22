package com.example.uxn_api.web.report.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReqDto {

    private String title;

    private String contents;

    private LocalDate createData;

    @JsonProperty("user_idx")
    private Long userIdx;

//    public Note toEntity(){
//        return Note
//                .builder()
//                .title(title)
//                .contents(contents)
//                .build();
//    }
}
