package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.StaffListResDto;
import com.example.uxn_api.web.staff.dto.res.StaffResDto;
import com.example.uxn_api.web.staff.dto.res.UnRecognizeList;
import com.example.uxn_common.global.domain.staff.Hospital;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserStaffMapping;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StaffInfoApiController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class StaffInfoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // UserHomeController 잡고 있는 Bean 객체에 대해 Mock 형태의 객체를 생성해줌
    @MockBean
    StaffService staffService;;
    @MockBean
    MailSendService mailSendService;;
    @MockBean
    UserService userService;;

    @Test
    @WithMockUser
    @DisplayName("userRegist test")
    void userRegistTest() throws Exception{

        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .code("code")
                .build();

        given(staffService.registration(userRegistrationDto, false))
                .willReturn(userRegistrationDto);


        Gson gson = new Gson();
        String content = gson.toJson(userRegistrationDto);
//        String content= objectMapper.writeValueAsString(saveReqDto);

        String getUrl = String.format("/api/v1/user/staff-info/registration");

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andDo(print());


        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(staffService).registration(userRegistrationDto, false);
    }

    @Test
    @WithMockUser
    @DisplayName("userRemoveRegist test")
    void userRemoveRegistTest() throws Exception{

        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .staffName("staff")
                .userEmail("abc123@gmail.com")
                .staffIdx(123L)
                .userIdx(33L)
                .code("code")
                .build();

        //return void
        long userIdx = 33L;
        long staffIdx = 111L;

        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("abc123@gmail.com")
                .build();


        given(staffService.detailStaff(userRegistrationDto.getStaffIdx())).willReturn(staff);
//        given(staffService.deRegistrationUser(userIdx, staffIdx)).willReturn(void);
//        doNothing().when(staffService).deRegistrationUser(userIdx, staffIdx);


        Gson gson = new Gson();
        String content = gson.toJson(userRegistrationDto);
//        String content= objectMapper.writeValueAsString(saveReqDto);

        String getUrl = String.format("/api/v1/user/staff-info/remove/registration");

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());


        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
//        verify(staffService).detailStaff(userRegistrationDto.getStaffIdx());
//        verify(staffService).deRegistrationUser(userIdx, staffIdx);
    }

    @Test
    @WithMockUser
    @DisplayName("staffList test")
    void staffListTest() throws Exception{
//        long userID = 33;
        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("abc123@gmail.com")
                .build();

//        Hospital hospital = new Hospital("uxn hospital", "수원시 영통구", "123-4567");
        StaffListResDto staffListResDto = new StaffListResDto(staff);


        List<StaffListResDto> staffList = new ArrayList<>();
        staffList.add(staffListResDto);
        given(staffService.staffList()).willReturn(staffList);

//        Gson gson = new Gson();
//        String content = gson.toJson(staff);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/staff-info/list")
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(this.staffService).staffList();
    }

    @Test
    @WithMockUser
    @DisplayName("recognize test")
    void recognizeTest() throws Exception{
        Long idx = 33L;
        Long staff = 111L;
        String getUrl = String.format("/api/v1/user/staff-info/recognize/%d/%d",idx, staff);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(this.staffService).checkLink(staff, idx);
    }

    @Test
    @WithMockUser
    @DisplayName("unRecognizeList test")
    void unRecognizeListTest() throws Exception{

        Long staffId = 111L;

        User user = User.builder()
                .idx(111L)
                .userName("user")
                .email("user123@gmail.com")
                .build();

//        Hospital hospital = new Hospital("uxn hospital", "수원시 영통구", "123-4567");
        UnRecognizeList unRecognizeList = new UnRecognizeList(user);

        List<UnRecognizeList> staffList = new ArrayList<>();
        staffList.add(unRecognizeList);

        given(staffService.unRecognizeList(staffId)).willReturn(staffList);

//        Gson gson = new Gson();
//        String content = gson.toJson(user);

        String getUrl = String.format("/api/v1/user/staff-info/un-recognize/list/" + staffId);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(this.staffService).unRecognizeList(staffId);
    }

    @Test
    @WithMockUser
    @DisplayName("sendLink test")
    void sendLinkTest() throws Exception{
        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .build();

        given(staffService.findByEmail(staff.getEmail())).willReturn(staff);
//        given(mailSendService.verificationMailSend(email, 3, 111L, null)).willReturn(null);

//        Gson gson = new Gson();
//        String content = gson.toJson(user);

        String getUrl = String.format("/api/v1/user/staff-info/send-mail/"+ staff.getIdx());

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .param("email", staff.getEmail())
                        .param("userIdx", "111L")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(this.mailSendService).verificationMailSend(staff.getEmail(), 3, 111L, null);
    }

    @Test
    @WithMockUser
    @DisplayName("staffInfoResponse test")
    void staffInfoResponseTest() throws Exception{
        Long staffId = 111L;

        Staff staff = Staff.builder()
                .idx(staffId)
                .staffName("staff")
                .email("staff123@gmail.com")
                .build();

        StaffResDto staffResDto = new StaffResDto(staff);

        given(staffService.staffDetail(staffId)).willReturn(staffResDto);

//        Gson gson = new Gson();
//        String content = gson.toJson(user);

        String getUrl = String.format("/api/v1/user/staff-info/detail/"+ staffId);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        verify(this.staffService).staffDetail(staffId);
    }

    @Test
    @WithMockUser
    @DisplayName("staffInfoResDto test")
    void staffInfoResDtoTest() throws Exception{
        Long staffId = 111L;

        Staff staff = Staff.builder()
                .idx(staffId)
                .staffName("staff")
                .email("staff123@gmail.com")
                .build();

        StaffResDto staffResDto = new StaffResDto(staff);

        List<StaffResDto> staffList = new ArrayList<>();
        staffList.add(staffResDto);

        //입력 및 출력 정의(가상)
        given(staffService.staffDetailForUser(staffId)).willReturn(staffList);

//        Gson gson = new Gson();
//        String content = gson.toJson(user);

        String getUrl = String.format("/api/v1/user/staff-info/staff-detail/"+ staffId);


        //URL 호출 및 리턴 값 확인(ok)
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        //실제 url 호출 후 아래 함수가 실행되었는지 확인
        verify(this.staffService).staffDetailForUser(staffId);
    }
}

