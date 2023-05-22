package com.example.uxn_api.web.report.controller;



import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;

import com.example.uxn_api.web.report.dto.req.*;
import com.example.uxn_api.web.report.dto.res.*;
import com.example.uxn_api.service.report.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportApiController {

    private final ReportService reportService;
 
    @GetMapping("/list/{idx}")
    @ApiOperation(value = "리포트 목록 요청")
    public ResponseEntity<ReportItemWrapper> getReportItemList(@PathVariable Long idx, @RequestParam(required = false, defaultValue = "1") String page,
         @RequestParam(required = false) String start, @RequestParam(required = false) String end){
        int pageValue = 1;
        if(page != null){
            try {
                pageValue = Integer.valueOf(page);
            } catch (Exception e) {}
        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDateTime startTime = null;
//        LocalDateTime endTime = null;
//        if(StringUtils.hasText(start)){
//            startTime = LocalDateTime.parse(start,formatter);
//        } else {
//
//            startTime = LocalDateTime.of(LocalDateTime.now().getYear(),LocalDateTime.now().getMonthValue(),1,0,0,0,0);
//        }
//        if(StringUtils.hasText(end)){
//            endTime = LocalDateTime.parse(end,formatter);
//        } else {
//            endTime = LocalDateTime.now();
//        }
        ReportItemWrapper result = reportService.getReportItemList(idx,pageValue);

        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/detail/{idx}")
    @ApiOperation(value = "리포트 요청")
    public ResponseEntity<ReportDetailDto> getReport(@PathVariable Long idx, 
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        
            ReportDetailDto result = reportService.getReport(idx,startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/csv/down/{userEmail}")
    public void downloadCsv(
            @PathVariable String userEmail,
            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response
    ){
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Encoding", "UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
//        response.setHeader("Content-Disposition","attachment;filename=result_" + userEmail +"_"+LocalDate.now()+ ".csv");
        response.setHeader("Content-Disposition","attachment;filename=result_" + userEmail +"_" + startDate + "_" + endDate + ".csv");
        reportService.csvFile(startDate,endDate,userEmail,response);

    }

}
