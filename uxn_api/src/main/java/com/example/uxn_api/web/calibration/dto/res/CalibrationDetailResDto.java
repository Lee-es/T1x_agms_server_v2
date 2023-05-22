package com.example.uxn_api.web.calibration.dto.res;

import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.note.Note;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CalibrationDetailResDto {

    private final String title;

    private final String contents;
    private final LocalDateTime createdDateTime;
    public CalibrationDetailResDto(Calibration entity){
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.createdDateTime = entity.getCreatedDate();
    }
}
