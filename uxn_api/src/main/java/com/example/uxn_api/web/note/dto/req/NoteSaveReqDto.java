package com.example.uxn_api.web.note.dto.req;

import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteSaveReqDto {

    private String title;

    private String contents;

//    private User user;

    private String createData;


    private Long userid;

//    public Note toEntity(){
//        return Note
//                .builder()
//                .title(title)
//                .contents(contents)
//                .build();
//    }
}
