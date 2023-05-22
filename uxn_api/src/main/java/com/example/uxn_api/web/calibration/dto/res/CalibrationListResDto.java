package com.example.uxn_api.web.calibration.dto.res;

import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CalibrationListResDto {

    @JsonProperty("title") // json 프로퍼티 지정을 해주지 않으면 InvalidDefinitionException cannot deserialize from Object value deserialize(역직렬화) 하는 과정에서 문제가 발생할 수 있음.
                        // -> 생성자의 인자명은 컴파일시에 이름이 바뀌기 때문에 생성자를 찾지 못하는 경우가 있다
    private final String title;

    @JsonProperty("contents")
    private final String contents;

    @JsonProperty("created_date")
    private final LocalDateTime createdDateTime;

    public CalibrationListResDto(Calibration entity){
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.createdDateTime = entity.getCreatedDate();
    }
}
