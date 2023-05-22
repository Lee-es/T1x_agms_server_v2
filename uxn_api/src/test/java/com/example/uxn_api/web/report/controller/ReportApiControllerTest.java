package com.example.uxn_api.web.report.controller;

import com.example.uxn_api.service.report.ReportService;
import com.example.uxn_api.web.report.dto.res.ReportDetailDto;
import com.example.uxn_api.web.report.dto.res.ReportItemWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class ReportApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ReportService reportService;

    @Test
    @WithMockUser
    @DisplayName("getReportItemList test")
    void getReportItemListTest() throws Exception{
        Long idx = 33L;
        String page = "1";
        int pageValue = Integer.valueOf(page);

        //테스트용 가짜 데이터 주입
        ReportItemWrapper wrapper = new ReportItemWrapper();
        given(reportService.getReportItemList(idx, pageValue)).willReturn(wrapper);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/report/list/"+  idx)
                        .param("page", page)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(reportService).getReportItemList(idx, pageValue);//code가 다름(랜덤)
    }

    @Test
    @WithMockUser
    @DisplayName("getReport test")
    void getReportTest() throws Exception{
        Long idx = 33L;

        LocalDate startDate = LocalDate.now().minusDays(3);//For reference
        LocalDate endDate = LocalDate.now();//For reference
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        String strStartDate = startDate.format(formatter);
        String strEndDate = endDate.format(formatter);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        ReportDetailDto result = ReportDetailDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        given(reportService.getReport(idx, startDate, endDate)).willReturn(result);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/report/detail/"+  idx)
                        .param("startDate", strStartDate)
                        .param("endDate", strEndDate)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(reportService).getReport(idx, startDate, endDate);
    }

    @Test
    @WithMockUser
    @DisplayName("downloadCsv test")
    void downloadCsvTest() throws Exception{
        String email = "abc123@gmail.com";
//        HttpServletResponse response = mock(HttpServletResponse.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        LocalDate startDate = LocalDate.now().minusDays(3);//For reference
        LocalDate endDate = LocalDate.now();//For reference
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        String strStartDate = startDate.format(formatter);
        String strEndDate = endDate.format(formatter);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        doNothing().when(reportService).csvFile(startDate,endDate,email,response);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/report/csv/down/"+  email)
                        .param("startDate", strStartDate)
                        .param("endDate", strEndDate)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
//        verify(reportService).csvFile(startDate,endDate,email,response);//response?
    }
}