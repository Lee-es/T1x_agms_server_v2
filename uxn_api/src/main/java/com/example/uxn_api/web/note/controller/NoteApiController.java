package com.example.uxn_api.web.note.controller;

import com.example.uxn_api.service.note.NoteService;
import com.example.uxn_api.web.note.dto.req.NoteDeleteReqDto;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_api.web.note.dto.res.NoteListResDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/note")
@RequiredArgsConstructor
@Slf4j
public class NoteApiController {

    private final NoteService noteService;

    @PostMapping("/save")
    @ApiOperation(value = "이벤트 기록", notes = "노트 역할")
    public ResponseEntity<NoteSaveReqDto> save(@RequestBody NoteSaveReqDto reqDto){
//        log.debug("이벤트 기록:" + reqDto.getContents() + ", " + reqDto.getCreateData());

        return ResponseEntity.ok().body(noteService.save(reqDto));
    }
//    @PostMapping("/save")
//    @ApiOperation(value = "이벤트 기록", notes = "노트 역할")
//    public ResponseEntity<NoteSaveReqDto> save(String ){
//        log.debug("이벤트 기록:" + reqDto.getContents() + ", " + reqDto.getCreateData());
//
//        return ResponseEntity.ok().body(noteService.save(reqDto));
//    }
//

    @GetMapping("/read/{id}")
    @ApiOperation(value = "기록 열람")
    public ResponseEntity<NoteDetailResDto> read(@PathVariable Long id){
        return ResponseEntity.ok().body(noteService.read(id));
    }

    @GetMapping("/read-all/{userIdx}")
    @ApiOperation(value = "기록 리스트")
    public ResponseEntity<List<NoteListResDto>> readAll(@PathVariable Long userIdx){
        return ResponseEntity.ok().body(noteService.noteList(userIdx));
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "기록 수정")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody NoteSaveReqDto reqDto){
        return ResponseEntity.ok().body(noteService.update(id, reqDto));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "기록 삭제")
    public void delete(@PathVariable Long id){
        noteService.delete(id);
    }
}
