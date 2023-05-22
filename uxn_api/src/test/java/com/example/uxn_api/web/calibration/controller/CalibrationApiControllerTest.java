package com.example.uxn_api.web.calibration.controller;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//spring security 관련 예외 처리를 해야 테스트가 가능하다.(아래 링크 참조)
//https://velog.io/@cieroyou/WebMvcTest%EC%99%80-Spring-Security-%ED%95%A8%EA%BB%98-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0

//@WebMvcTest(CalibrationApiController.class)
@WebMvcTest(controllers = CalibrationApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class CalibrationApiControllerTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("## BeforeAll Annotation 호출 ##");
        System.out.println();
    }

    @AfterAll
    static void afterAll() {
        System.out.println("## afterAll Annotation 호출 ##");
        System.out.println();
    }
//    @Autowired
//    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // CalibrationApiController 잡고 있는 Bean 객체에 대해 Mock 형태의 객체를 생성해줌
//    @MockBean SpyBean
    @MockBean
    CalibrationService calibrationService;

    @Test
    @WithMockUser
    @DisplayName("calibration save test")
    void saveTest() throws Exception{
        long userID = 33;

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        //실제 DB를 사용하지 않고 Test 용 Mock DB를 생성하는것을 의미한다.
        given(calibrationService.save(new CalibrationSaveReqDto("calibration", "99", "2022-11-03 09:28:59:000000", userID)))
                .willReturn(new CalibrationSaveReqDto("calibration", "99", "2022-11-03 09:28:59:000000", userID));

        CalibrationSaveReqDto saveReqDto = CalibrationSaveReqDto.builder()
                .title("calibration")
                .contents("99")
                .createData("2022-11-03 09:28:59:000000")
                .userid(userID)
                .build();

        Gson gson = new Gson();
        String content = gson.toJson(saveReqDto);
//        String content= objectMapper.writeValueAsString(saveReqDto);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/calibration/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.TEXT_PLAIN)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(content().body(equalTo("Calibration")))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.contents").exists())
                .andExpect(jsonPath("$.createData").exists())
                .andExpect(jsonPath("$.userid").exists())
                .andDo(print());


        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(calibrationService).save(new CalibrationSaveReqDto(
                "calibration",
                "99",
                "2022-11-03 09:28:59:000000",
                userID));
    }

    @Test
    @WithMockUser
    @DisplayName("calibration read test")
    void readTest() throws Exception{
        long userID = 33;
        Calibration calibration = Calibration
                .builder()
                .user(null)
                .contents("99")
                .title("Calibration")
                .createdDate(null)
                .build();

        String getUrl = String.format("/api/v1/calibration/read/%d", userID);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(calibrationService.read(userID)).willReturn(new CalibrationDetailResDto(calibration));
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.contents").exists())
                .andDo(print());

        ((CalibrationService) Mockito.verify(this.calibrationService)).read(userID);
    }
}