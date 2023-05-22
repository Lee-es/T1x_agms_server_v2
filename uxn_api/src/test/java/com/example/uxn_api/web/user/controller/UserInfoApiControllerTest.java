package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.device.DeviceService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.device.dto.res.CheckDeviceResponse;
import com.example.uxn_api.web.device.dto.res.UserInfoResDto;
import com.example.uxn_api.web.staff.dto.res.StaffResDto;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserInfoApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class UserInfoApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    DeviceService deviceService;

    User user = User.builder()
            .idx(33L)
            .userName("user")
            .email("user123@gmail.com")
            .password("uxn1234!@")
            .emailVerifiedSuccess(false)
            .build();

    LocalDateTime createTime = LocalDateTime.now();

    Device device = Device.builder()
            .createDataTime(createTime)
            .user(user)
            .deviceId("deviceId")
            .diabetesLevel(99.99)
            .build();

    // List 준비
    List<Device> deviceList = Arrays.asList(device);

    // Set으로 변환 (생성자)
    Set<Device> setDevice = new HashSet<>(deviceList);

    CheckDeviceResponse devRes = new CheckDeviceResponse(device);

//    List<CheckDeviceResponse> checkDevices = new ArrayList<>();
//    checkDevices.add(devRes);
    User resUser = User.builder()
            .userName("user")
            .email("user123@gmail.com")
            .devices(setDevice)
            .build();

    @Test
    @WithMockUser
    @DisplayName("getUserInfo test")
    void getUserInfoTest() throws Exception{
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("getUserInfoByUserId test")
    void getUserInfoByUserIdTest() throws Exception {
        String userId = "userID";

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByUserId(userId)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/search/" + userId);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByUserId(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("getMedian test")
    void getMedianTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/median/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("month","12")
                        .param("day","27")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("getPercentileWithTime test")
    void getPercentileWithTimeTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        int hour = createTime.getHour();
        int min = createTime.getMinute();

        String getUrl = String.format("/api/v1/user/info/percentile/time/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("hour",Integer.toString(hour))
                        .param("minutes",Integer.toString(min))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("getPercent test")
    void getPercentTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/percent/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("getDiabetesList test")
    void getDiabetesListTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/diabetes-list/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("month","12")
                        .param("day","27")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("month","12")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("day","27")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("diabetesList test")
    void diabetesListTest() throws Exception {
        Long id = 33L;

        given(deviceService.diabetesList("25", "27", id)).willReturn(deviceList);

        String getUrl = String.format("/api/v1/user/info/diabetes-list/start-end/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("startDay","25")
                        .param("endDay","27")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(deviceService).diabetesList("25", "27", id);
    }

    @Test
    @WithMockUser
    @DisplayName("bloodSugarReport test")
    void bloodSugarReportTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);

        OptionalDouble average = OptionalDouble.of(99.9);
        given(deviceService.average(id)).willReturn(average);

        double activePercent = 33.3;
        given(deviceService.activatePercent(id)).willReturn(activePercent);

        String getUrl = String.format("/api/v1/user/info/report/blood/sugar/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(deviceService).average(id);
//        verify(deviceService).activatePercent(id);
    }

    @Test
    @WithMockUser
    @DisplayName("percentAndTime test")
    void percentAndTimeTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        //컨트롤러 List Parameter 참고
        //http://localhost:8080/multi?userIds=1,2,3,4,5 다음과 같이 ','로 구분해서 url에 넣어주면 파라미터로 List를 받을 수 있다.
        //혹은 http://localhost:8080/multi?userIds=1&userIds=2&userIds=3와 같이 사용할 수도 있다.

        // Map 대신 객체를 사용해도 된다.
//        List<Integer> range = Arrays.asList(70, 180, 70, 54, 180, 250);
//        Map<String, Object> map = new HashMap<>();
//        map.put("range2", range);

//        Gson gson = new Gson();
//        String content = gson.toJson(map);

        String getUrl = String.format("/api/v1/user/info/agp/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("range", "70", "180", "70", "54", "180", "250")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("average test")
    void averageTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/diabetes/average/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);
    }

    @Test
    @WithMockUser
    @DisplayName("activate test")
    void activateTest() throws Exception {
        Long id = 33L;

        UserInfoResDto req = new UserInfoResDto(this.resUser);
        given(userService.findByIdx(id)).willReturn(req);

        String getUrl = String.format("/api/v1/user/info/activate/percent/" + id);
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("day", "3")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(userService).findByIdx(id);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .param("day", null)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}