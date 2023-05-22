package com.example.uxn_api.web.calibration.controller;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.note.NoteService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationListResDto;
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
@RequestMapping("/api/v1/calibration")
@RequiredArgsConstructor
@Slf4j
public class CalibrationApiController {

    private final CalibrationService calibrationService;

    @PostMapping("/save")
    @ApiOperation(value = "Calibration 기록", notes = "Calibration 역할")
    public ResponseEntity<CalibrationSaveReqDto> save(@RequestBody CalibrationSaveReqDto reqDto){
//        log.debug("Calibration 기록:" + reqDto.getContents() + ", " + reqDto.getCreateData());

        return ResponseEntity.ok().body(calibrationService.save(reqDto));
    }

    @GetMapping("/read/{id}")
    @ApiOperation(value = "기록 열람")
    public ResponseEntity<CalibrationDetailResDto> read(@PathVariable Long id){
//        log.debug("Calibration 열람");

        return ResponseEntity.ok().body(calibrationService.read(id));
    }

    @GetMapping("/read-all/{userIdx}")
    @ApiOperation(value = "기록 리스트")
    public ResponseEntity<List<CalibrationListResDto>> readAll(@PathVariable Long userIdx){
        return ResponseEntity.ok().body(calibrationService.noteList(userIdx));
    }

    @PutMapping("/update/{id}")
    @ApiOperation(value = "기록 수정")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CalibrationSaveReqDto reqDto){
        return ResponseEntity.ok().body(calibrationService.update(id, reqDto));
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "기록 삭제")
    public void delete(@PathVariable Long id){
        calibrationService.delete(id);
    }

}
