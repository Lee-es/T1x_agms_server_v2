package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.staff.dto.req.StaffSignUpReqDto;
import com.example.uxn_api.web.user.dto.req.AdminSignUpReqDto;
import com.example.uxn_api.web.user.dto.req.UserSignUpReqDto;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.Role;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SignUpApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class SignUpApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;
    @MockBean
    StaffService staffService;
    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    @DisplayName("userSave test")
    void userSaveTest() throws Exception{

        UserSignUpReqDto reqDto = UserSignUpReqDto.builder()
                .userName("user")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        User user = User
                .builder()
//                .idx(33L)
                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .gender(reqDto.getGender())
                .role(Role.USER)
                .birth(reqDto.getBirth())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(userService.saveUser(user)).willReturn(user);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).saveUser(user);
        verify(this.userService).addAuthority(user.getIdx(), "ROLE_USER");

        //Case2
        UserSignUpReqDto reqDto2 = UserSignUpReqDto.builder()
                .userName("staff")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();


        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case
        Staff chkStaff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("user1234@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(staffService.findByEmail(reqDto2.getEmail())).willReturn(chkStaff);

        User user2 = User
                .builder()
//                .idx(33L)
                .userName(reqDto2.getUserName())
                .email(reqDto2.getEmail())
                .password(passwordEncoder.encode(reqDto2.getPassword()))
                .gender(reqDto2.getGender())
                .role(Role.USER)
                .birth(reqDto2.getBirth())
                .enabled(true)
                .authorities(reqDto2.getAuthorities())
                .build();

        given(userService.saveUser(user2)).willReturn(user2);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        content = gson.toJson(reqDto2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).addAuthority(user2.getIdx(), "ROLE_USER");

        //Case3
        UserSignUpReqDto reqDto3 = UserSignUpReqDto.builder()
                .userName("user")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime2 = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case

        User chkUser = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime2)
                .build();

        given(userService.findByEmail(reqDto3.getEmail())).willReturn(chkUser);

        User user3 = User
                .builder()
//                .idx(33L)
                .userName(reqDto3.getUserName())
                .email(reqDto3.getEmail())
                .password(passwordEncoder.encode(reqDto3.getPassword()))
                .gender(reqDto3.getGender())
                .role(Role.USER)
                .birth(reqDto3.getBirth())
                .enabled(true)
                .authorities(reqDto3.getAuthorities())
                .build();

        given(userService.saveUser(user3)).willReturn(user3);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        content = gson.toJson(reqDto3);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).addAuthority(user3.getIdx(), "ROLE_USER");

        //Case4
        UserSignUpReqDto reqDto4 = UserSignUpReqDto.builder()
                .userName("user")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime4 = LocalDateTime.now().minusMinutes(10);//10분전 가입 신청한 이력

        User chkUser4 = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime4)
                .build();

        given(userService.findByEmail(reqDto4.getEmail())).willReturn(chkUser4);

        User user4 = User
                .builder()
//                .idx(33L)
                .userName(reqDto4.getUserName())
                .email(reqDto4.getEmail())
                .password(passwordEncoder.encode(reqDto4.getPassword()))
                .gender(reqDto4.getGender())
                .role(Role.USER)
                .birth(reqDto4.getBirth())
                .enabled(true)
                .authorities(reqDto4.getAuthorities())
                .build();

        given(userService.saveUser(user4)).willReturn(user4);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

//        Gson gson = new Gson();
        content = gson.toJson(reqDto4);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).saveUser(user);
//        verify(this.userService).addAuthority(user.getIdx(), "ROLE_USER");
    }

    //@Test
    @WithMockUser
    @DisplayName("userSave Case2 test")
    void userSaveCase2Test() throws Exception{

        UserSignUpReqDto reqDto = UserSignUpReqDto.builder()
                .userName("staff")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();


        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case
        Staff chkStaff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("user1234@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(staffService.findByEmail(reqDto.getEmail())).willReturn(chkStaff);

//        User chkUser = User.builder()
//                .idx(33L)
//                .userName("user")
//                .email("user123@gmail.com")
//                .password("uxn1234!@")
//                .build();

//        given(userService.findByEmail(reqDto.getEmail())).willReturn(chkUser);

        User user = User
                .builder()
//                .idx(33L)
                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .gender(reqDto.getGender())
                .role(Role.USER)
                .birth(reqDto.getBirth())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(userService.saveUser(user)).willReturn(user);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.userService).addAuthority(user.getIdx(), "ROLE_USER");
    }

    //@Test
    @WithMockUser
    @DisplayName("userSave Case3 test")
    void userSaveCase3Test() throws Exception{

        UserSignUpReqDto reqDto = UserSignUpReqDto.builder()
                .userName("user")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case

        User chkUser = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(userService.findByEmail(reqDto.getEmail())).willReturn(chkUser);

        User user = User
                .builder()
//                .idx(33L)
                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .gender(reqDto.getGender())
                .role(Role.USER)
                .birth(reqDto.getBirth())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(userService.saveUser(user)).willReturn(user);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.userService).addAuthority(user.getIdx(), "ROLE_USER");
    }

    //@Test
    @WithMockUser
    @DisplayName("userSave Case4(email duplication) test")
    void userSaveCase4Test() throws Exception{

        UserSignUpReqDto reqDto = UserSignUpReqDto.builder()
                .userName("user")
                .email("user1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime = LocalDateTime.now().minusMinutes(10);//10분전 가입 신청한 이력

        User chkUser = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(userService.findByEmail(reqDto.getEmail())).willReturn(chkUser);

        User user = User
                .builder()
//                .idx(33L)
                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .gender(reqDto.getGender())
                .role(Role.USER)
                .birth(reqDto.getBirth())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(userService.saveUser(user)).willReturn(user);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/user/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isPayloadTooLarge())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).saveUser(user);
//        verify(this.userService).addAuthority(user.getIdx(), "ROLE_USER");

    }

    @Test
    @WithMockUser
    @DisplayName("staffSave test")
    void staffSaveTest() throws Exception{

        StaffSignUpReqDto reqDto = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff1234@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        Staff staff = Staff
                .builder()
                .staffName(reqDto.getStaffName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .hospital(reqDto.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .birth(reqDto.getBirth())
                .phoneNumber(reqDto.getPhoneNumber())
                .gender(reqDto.getGender())
                .approvalCode(reqDto.getApprovalCode())
                .build();

        given(staffService.save(staff)).willReturn(staff);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).save(staff);
        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

        //Case2
        StaffSignUpReqDto reqDto2 = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        //staff 이메일과 중복체크
        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case
        Staff chkStaff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(staffService.findByEmail(reqDto2.getEmail())).willReturn(chkStaff);

        Staff staff2 = Staff
                .builder()
                .staffName(reqDto2.getStaffName())
                .email(reqDto2.getEmail())
                .password(passwordEncoder.encode(reqDto2.getPassword()))
                .hospital(reqDto2.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto2.getAuthorities())
                .birth(reqDto2.getBirth())
                .phoneNumber(reqDto2.getPhoneNumber())
                .gender(reqDto2.getGender())
                .approvalCode(reqDto2.getApprovalCode())
                .build();

        given(staffService.save(staff2)).willReturn(staff2);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

//        Gson gson = new Gson();
        content = gson.toJson(reqDto2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

        //Case3
        StaffSignUpReqDto reqDto3 = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime3 = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case

        //user 이메일과 중복체크
        User chkUser = User.builder()
                .idx(33L)
                .userName("user")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime3)
                .build();

        given(userService.findByEmail(reqDto.getEmail())).willReturn(chkUser);

        Staff staff3 = Staff
                .builder()
                .staffName(reqDto3.getStaffName())
                .email(reqDto3.getEmail())
                .password(passwordEncoder.encode(reqDto3.getPassword()))
                .hospital(reqDto3.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto3.getAuthorities())
                .birth(reqDto3.getBirth())
                .phoneNumber(reqDto3.getPhoneNumber())
                .gender(reqDto3.getGender())
                .approvalCode(reqDto3.getApprovalCode())
                .build();

        given(staffService.save(staff3)).willReturn(staff3);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

//        Gson gson = new Gson();
        content = gson.toJson(reqDto3);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).save(staff);
//        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

        //Case4
        StaffSignUpReqDto reqDto4 = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime4 = LocalDateTime.now().minusMinutes(10);//10분전 가입 신청한 이력

        Staff chkStaff4 = Staff.builder()
                .idx(33L)
                .staffName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime4)
                .build();

        given(staffService.findByEmail(reqDto4.getEmail())).willReturn(chkStaff4);

        Staff staff4 = Staff
                .builder()
                .staffName(reqDto4.getStaffName())
                .email(reqDto4.getEmail())
                .password(passwordEncoder.encode(reqDto4.getPassword()))
                .hospital(reqDto4.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto4.getAuthorities())
                .birth(reqDto4.getBirth())
                .phoneNumber(reqDto4.getPhoneNumber())
                .gender(reqDto4.getGender())
                .approvalCode(reqDto4.getApprovalCode())
                .build();

        given(staffService.save(staff4)).willReturn(staff4);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

//        Gson gson = new Gson();
        content = gson.toJson(reqDto4);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

    }

    //@Test
    @WithMockUser
    @DisplayName("staffSave Case2 test")
    void staffSaveCase2Test() throws Exception{

        StaffSignUpReqDto reqDto = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        //staff 이메일과 중복체크
        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case
        Staff chkStaff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(staffService.findByEmail(reqDto.getEmail())).willReturn(chkStaff);

        Staff staff = Staff
                .builder()
                .staffName(reqDto.getStaffName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .hospital(reqDto.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .birth(reqDto.getBirth())
                .phoneNumber(reqDto.getPhoneNumber())
                .gender(reqDto.getGender())
                .approvalCode(reqDto.getApprovalCode())
                .build();

        given(staffService.save(staff)).willReturn(staff);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");
    }

    //@Test
    @WithMockUser
    @DisplayName("staffSave Case3 test")
    void staffSaveCase3Test() throws Exception{

        StaffSignUpReqDto reqDto = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime = LocalDateTime.now().minusMinutes(31);//31분전 가입 시간이 지난 case

        //user 이메일과 중복체크
        User chkUser = User.builder()
                .idx(33L)
                .userName("user")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(userService.findByEmail(reqDto.getEmail())).willReturn(chkUser);

        Staff staff = Staff
                .builder()
                .staffName(reqDto.getStaffName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .hospital(reqDto.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .birth(reqDto.getBirth())
                .phoneNumber(reqDto.getPhoneNumber())
                .gender(reqDto.getGender())
                .approvalCode(reqDto.getApprovalCode())
                .build();

        given(staffService.save(staff)).willReturn(staff);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).save(staff);
        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

    }

//    @Test
    @WithMockUser
    @DisplayName("staffSave Case4(email duplication) test")
    void staffSaveCase4Test() throws Exception{

        StaffSignUpReqDto reqDto = StaffSignUpReqDto.builder()
                .staffId("staffId")
                .staffName("staffName")
                .email("staff123@gmail.com")
                .birth("1999-01-01")
                .password("uxn1234!@")
                .build();

        LocalDateTime curTime = LocalDateTime.now().minusMinutes(10);//10분전 가입 신청한 이력

        Staff chkStaff = Staff.builder()
                .idx(33L)
                .staffName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(curTime)
                .build();

        given(staffService.findByEmail(reqDto.getEmail())).willReturn(chkStaff);

        Staff staff = Staff
                .builder()
                .staffName(reqDto.getStaffName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .hospital(reqDto.getHospital())
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .birth(reqDto.getBirth())
                .phoneNumber(reqDto.getPhoneNumber())
                .gender(reqDto.getGender())
                .approvalCode(reqDto.getApprovalCode())
                .build();

        given(staffService.save(staff)).willReturn(staff);
//        given(userService.addAuthority(user.getIdx(), "ROLE_USER")).willReturn(null);//void

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/staff/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.staffService).addAuthority(staff.getIdx(), "ROLE_STAFF");

    }

    @Test
    @WithMockUser
    @DisplayName("withdrawalUser test")//user 회원 탈퇴
    void withdrawalUserTest() throws Exception {

        //삭제 테스트할 user 정보
        User user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user23@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        given(userService.findByEmail(user.getEmail())).willReturn(user);
//        given(userService.withdrawal(user.getEmail())).willReturn(null);//return void

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/withdrawal/user/" + user.getEmail())
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.userService).withdrawal(user.getEmail());//객체를 리턴하지 않음

        //Case2
        //삭제 테스트할 user 정보
        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        given(staffService.findByEmail(staff.getEmail())).willReturn(staff);
//        given(staffService.deleteStaff(staff)).willReturn(anyInt());//return void
//        Mockito.when(staffService.deleteStaff(staff)).thenReturn(void);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/withdrawal/user/" + staff.getEmail())
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.staffService).deleteStaff(staff);//객체를 리턴하지 않음
    }

//    @Test
    @WithMockUser
    @DisplayName("withdrawalUser Case2 test")//staff 회원 탈퇴
    void withdrawalUserCase2Test() throws Exception {

        //삭제 테스트할 user 정보
        Staff staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        given(staffService.findByEmail(staff.getEmail())).willReturn(staff);
//        given(staffService.deleteStaff(staff)).willReturn(anyInt());//return void
//        Mockito.when(staffService.deleteStaff(staff)).thenReturn(void);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/withdrawal/user/" + staff.getEmail())
//                        .content(content)
//                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.staffService).deleteStaff(staff);//객체를 리턴하지 않음
    }

    @Test
    @WithMockUser
    @DisplayName("adminSave test")//staff 회원 탈퇴
    void adminSaveTest() throws Exception {

        //삭제 테스트할 user 정보
        AdminSignUpReqDto reqDto = AdminSignUpReqDto.builder()
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
//                .gender()
                .build();

        User user = User.builder()
//                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .role(Role.ADMIN)
                .gender(reqDto.getGender())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(staffService.findByEmail(reqDto.getEmail())).willReturn(null);
        given(userService.findByEmail(reqDto.getEmail())).willReturn(null);
//        given(staffService.deleteStaff(staff)).willReturn(anyInt());//return void
//        Mockito.when(staffService.deleteStaff(staff)).thenReturn(void);

        given(userService.saveUser(user)).willReturn(user);

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/admin/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())//이메일 중복 exception
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

        verify(this.userService).addAuthority(user.getIdx(), "ROLE_ADMIN");//객체를 리턴하지 않음

        //Case2
        //삭제 테스트할 user 정보
        AdminSignUpReqDto reqDto2 = AdminSignUpReqDto.builder()
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
//                .gender()
                .build();

        User user2 = User.builder()
//                .userName(reqDto.getUserName())
                .email(reqDto2.getEmail())
                .password(passwordEncoder.encode(reqDto2.getPassword()))
                .role(Role.ADMIN)
                .gender(reqDto2.getGender())
                .enabled(true)
                .authorities(reqDto2.getAuthorities())
                .build();

        given(userService.findByEmail(reqDto2.getEmail())).willReturn(user2);

//        Gson gson = new Gson();
        content = gson.toJson(reqDto2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/admin/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())//이메일 중복 exception
                .andDo(print());
    }

    //@Test
    @WithMockUser
    @DisplayName("adminSave Case2 test")//staff 회원 탈퇴
    void adminSaveCase2Test() throws Exception {

        //삭제 테스트할 user 정보
        AdminSignUpReqDto reqDto = AdminSignUpReqDto.builder()
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
//                .gender()
                .build();

        User user = User.builder()
//                .userName(reqDto.getUserName())
                .email(reqDto.getEmail())
                .password(passwordEncoder.encode(reqDto.getPassword()))
                .role(Role.ADMIN)
                .gender(reqDto.getGender())
                .enabled(true)
                .authorities(reqDto.getAuthorities())
                .build();

        given(userService.findByEmail(reqDto.getEmail())).willReturn(user);

        Gson gson = new Gson();
        String content = gson.toJson(reqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/sign-up/admin/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())//이메일 중복 exception
                .andDo(print());
    }
}