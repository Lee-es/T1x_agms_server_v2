package com.example.uxn_api.web.device.controller;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.device.DeviceService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.calibration.controller.CalibrationApiController;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.device.dto.req.DeviceRegistrationDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_api.web.staff.dto.res.StaffListResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class DeviceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    DeviceService deviceService;

    @MockBean
    UserRepository userRepository;
    @BeforeEach
    public void setUpTest() {

    }

    @Test
    @WithMockUser
    @DisplayName("registration test")
    void registrationTest() throws Exception{

//        LocalDateTime now = LocalDateTime.now();
//
//        DeviceRegistrationDto dto = DeviceRegistrationDto.builder()
//                .deviceId("device_id")
//                .diabetesLevel(99.9)
//                .ae_current(-1)
//                .ref(0.6932)
//                .we_p(0.9018)
//                .ae_p(-1)
//                .batteryLevel(2.85)
//                .createDataTime(now)
//                .user_id(33L)
//                .build();
//
//        User user = User.builder()
//                .idx(33L)
//                .userName("user")
//                .email("user1234@gmail.com")
//                .build();
//
////        Gson gson = new Gson();
////        String content = gson.toJson(dto);
//        String content = objectMapper.writeValueAsString(dto);//json이 아닐경우
//
////        Case1
//
//        //실제 DB를 사용하지 않고 Test 용 Mock DB를 생성하는것을 의미한다.
//        given(userRepository.findById(dto.getUser_id())).willReturn(Optional.ofNullable(user));
//
//        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        TimeZone tzSeoul = TimeZone.getTimeZone("Asia/Seoul");
//        String timeNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(tzSeoul.toZoneId()));
//        LocalDateTime d = LocalDateTime.parse(timeNow, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
//        dto.setCreateDataTime(d);
//
//        given(deviceService.registration(dto)).willReturn(dto);
//
//        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/diabetes/save/diabetes-info")
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("turnOn", "true")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andDo(print());
//
////        verify(deviceService).registration(dto);
//
////        Case2
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/diabetes/save/diabetes-info")
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("turnOn", "false")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("infoFromUser test")
    void infoFromUserTest() throws Exception{
        long userIdx = 33;

        User user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user1234@gmail.com")
                .build();

        Device entity = Device.builder()
                .user(user)
                .deviceId("device_id")
                .diabetesLevel(99.9)
                .build();

        UserInfoResponse uerInfoResponse = new UserInfoResponse(entity);

        List<UserInfoResponse> userInfoResponseList = new ArrayList<>();
        userInfoResponseList.add(uerInfoResponse);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(deviceService.diabetesInfo(userIdx)).willReturn(userInfoResponseList);

        String getUrl = String.format("/api/v1/diabetes/user-info/%d", userIdx);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl))
                .andExpect(status().isOk())
//                .andExpect(content().string("device_Id"))
                .andDo(print());

        verify(deviceService).diabetesInfo(userIdx);
    }
}