package com.example.uxn_api.web.note.dto.res;

import com.example.uxn_common.global.domain.note.Note;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoteDetailResDto {

    private final String title;

    private final String contents;
    private final LocalDateTime createdDateTime;

    public NoteDetailResDto (Note entity){
        this.title = entity.getTitle();
        this.contents = entity.getContents();
        this.createdDateTime = entity.getCreateDate();
    }
}
