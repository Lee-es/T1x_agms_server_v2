package com.example.uxn_api.web.staff.controller;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.email.MailApiController;
import com.example.uxn_api.web.staff.dto.req.StaffUpdateReqDto;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.AgpReportDto;
import com.example.uxn_api.web.staff.dto.res.StaffResDto;
import com.example.uxn_api.web.staff.dto.res.UserInfoResponseDto;
import com.example.uxn_api.web.staff.dto.res.UserListForStaff;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StaffApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})
class StaffApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    StaffService staffService;
    @MockBean
    MailSendService mailSendService;
    private Staff staff;

    @BeforeEach
    public void setUpTest() {
        staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff1234@gmail.com")
                .approvalCode("testCode")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("staffRegistrationUser test")
    void staffRegistrationUserTest() throws Exception{

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .staffName("staff")
                .userEmail("staff123@gmail.com")
                .staffIdx(111L)
                .userIdx(33L)
                .build();

        given(staffService.detailStaff(registrationDto.getStaffIdx())).willReturn(staff);

        Gson gson = new Gson();
        String content = gson.toJson(registrationDto);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/staff/registration/user")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isGone())
                .andDo(print());

        //case2
        UserRegistrationDto registrationDto2 = UserRegistrationDto.builder()
                .staffName("staff")
                .userEmail("staff123@gmail.com")
                .staffIdx(111L)
                .userIdx(33L)
                .code("testCode")
                .build();

        String content2 = gson.toJson(registrationDto2);

        given(staffService.detailStaff(registrationDto2.getStaffIdx())).willReturn(staff);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/staff/registration/user")
                        .content(content2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isLengthRequired())
                .andDo(print());

        //case3
        UserRegistrationDto registrationDto3 = UserRegistrationDto.builder()
                .code("testCode123")
                .build();

        given(staffService.detailStaff(registrationDto3.getStaffIdx())).willReturn(staff);

        String content3 = gson.toJson(registrationDto3);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/staff/registration/user")
                        .content(content3)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isPreconditionFailed())
                .andDo(print());

        //case4
        UserRegistrationDto registrationDto4 = UserRegistrationDto.builder()
                .code("testCode")
                .build();

        given(staffService.detailStaff(registrationDto4.getStaffIdx())).willReturn(staff);

        String content4 = gson.toJson(registrationDto4);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/staff/registration/user")
                        .content(content4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
//        verify(staffService).detailStaff(registrationDto3.getStaffIdx());
        verify(staffService).registration(registrationDto4, true);
    }

    @Test
    @WithMockUser
    @DisplayName("userDetail test")
    void userDetailTest() throws Exception{
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

        Set<Device> setDevice = new HashSet<>(deviceList);

        User reqUser = User.builder()
                .userName("user")
                .email("user123@gmail.com")
                .devices(setDevice)
                .build();

        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(reqUser);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.userInfo(reqUser.getUsername())).willReturn(userInfoResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/user-detail/" + reqUser.getUsername())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(staffService).userInfo(reqUser.getUsername());
    }

    @Test
    @WithMockUser
    @DisplayName("staffDetail test")
    void staffDetailTest() throws Exception{
        Long userID = 33L;

        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .build();

        StaffResDto staffResDto = new StaffResDto(staff);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.staffDetail(userID)).willReturn(staffResDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/detail/" + userID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("userList test")
    void userListTest() throws Exception{
        Long userID = 33L;

        Staff staff = Staff
                .builder()
                .staffName("staff")
                .idx(111L)
                .build();

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.detailStaff(userID)).willReturn(staff);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/user-list/" + userID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @WithMockUser
    @DisplayName("unReconizeUserList test")
    void unReconizeUserListTest() throws Exception{
        Long userID = 33L;

        Staff staff = Staff
                .builder()
                .staffName("staff")
                .idx(111L)
                .build();

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.detailStaff(userID)).willReturn(staff);

        User user = User.builder()
                .idx(userID)
                .userName("user")
                .build();

        List<User> userList = new ArrayList<> ();
//        userList.add(user);

        given(staffService.getUserList(staff.getIdx(), 0)).willReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/user-list/unreconize/" + userID)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
//        verify(staffService).staffDetail(userID);
    }

    @Test
    @WithMockUser
    @DisplayName("searchUserName test")
    void searchUserNameTest() throws Exception{
        String userName = "userName";

        UserListForStaff userListForStaff = UserListForStaff
                .builder()
                .userIdx(33L)
                .userName(userName)
                .build();

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.userResult(userName)).willReturn(userListForStaff);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/search/" + userName)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(staffService).userResult(userName);
    }

    @Test
    @WithMockUser
    @DisplayName("snapShot test")
    void snapShotTest() throws Exception{
        String userName = "userName";

        UserListForStaff userListForStaff = UserListForStaff
                .builder()
                .userIdx(33L)
                .userName(userName)
                .build();

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.userResult(userName)).willReturn(userListForStaff);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/snap-shot/" + userName)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(staffService).userResult(userName);
    }

    @Test
    @WithMockUser
    @DisplayName("agpReport test")
    void agpReportTest() throws Exception{

        String userName = "userName";
        String startDay = "27";
        String endDay = "30";

        AgpReportDto agpReportDto = AgpReportDto.builder()
                .userName(userName)
                .build();

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.agpReportDto(userName, startDay, endDay)).willReturn(agpReportDto);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/staff/agp-report/" + userName)
                        .param("startDay", startDay)
                        .param("endDay", endDay)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(staffService).agpReportDto(userName, startDay, endDay);
    }

    @Test
    @WithMockUser
    @DisplayName("signUp test")
    void signUpTest() throws Exception{

        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .build();

        given(staffService.findByEmail(staff.getEmail())).willReturn(staff);

        StaffUpdateReqDto staffUpdateReqDto =
                StaffUpdateReqDto.builder()
                        .staffName("staff")
                        .email("staff123@gmail.com")
                        .phoneNumber("02-123-4597")
                        .build();

        Gson gson = new Gson();
        String content = gson.toJson(staffUpdateReqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/staff/update")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }
}