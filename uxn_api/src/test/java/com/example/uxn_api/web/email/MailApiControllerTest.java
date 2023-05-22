package com.example.uxn_api.web.email;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.calibration.controller.CalibrationApiController;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_common.global.domain.email.repository.InviteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.ChangePasswordTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MailApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class MailApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    StaffService staffService;

    @MockBean
    MailSendService mailSendService;

    @MockBean
    InviteRepository inviteRepository;

    @MockBean
    ChangePasswordTokenRepository changePasswordTokenRepository;

    @MockBean
    LogService logService;

    private static final Long MAX_EXPIRE_TIME = 30L;
//    private static final Long MAX_EXPIRE_TIME = 1L;

    private User user;
    private Staff staff;


    @BeforeEach
    public void setUpTest() {
        user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user1234@gmail.com")
                .emailVerifyCode("testCode")
                .build();

        staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff1234@gmail.com")
                .emailVerifyCode("testCode")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("mailSend test")
    void mailSendTest() throws Exception{

        //테스트용 가짜 데이터 주입
        String email = "user1234@gmail.com";
        String code = "testCode";//MailSendService.createKey();
        Long index = 33L;

        given(userService.findByEmail(email)).willReturn(user);
        doNothing().when(mailSendService).verificationMailSend(email,1, index,code);
        doNothing().when(userService).emailVerifyCode(code, email);
        doNothing().when(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/mail/send")
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email,1, index,code);//code가 다름(랜덤)
//        verify(userService).emailVerifyCode(code, email);//code가 다름(랜덤)
        verify(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);//code가 다름(랜덤)

        //Case2
        //테스트용 가짜 데이터 주입
        email = "staff1234@gmail.com";
        code = "testCode";//MailSendService.createKey();
        index = 111L;

        given(staffService.findByEmail(email)).willReturn(staff);
        doNothing().when(mailSendService).verificationMailSend(email,1, index,code);
        doNothing().when(staffService).emailVerifyCode(code, email);
        doNothing().when(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/mail/send")
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email,1, index,code);//code가 다름(랜덤)
//        verify(userService).emailVerifyCode(code, email);//code가 다름(랜덤)
        verify(logService).log(false, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);//code가 다름(랜덤)

        //Case3
        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        email = "staff1234@gmail.com";
        //code = "testCode";//MailSendService.createKey();
        //index = 33L;

        doNothing().when(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증, user not found", email,null,null,null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/mail/send")
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email,1, index,code);//code가 다름(랜덤)
//        verify(userService).emailVerifyCode(code, email);//code가 다름(랜덤)
//        verify(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증, user not found", email,null,null,null);//code가 다름(랜덤)

    }

//    @Test
    @WithMockUser
    @DisplayName("mailSend case2 test")
    void mailSendCase2Test() throws Exception{

        //테스트용 가짜 데이터 주입
        String email = "staff1234@gmail.com";
        String code = "testCode";//MailSendService.createKey();
        Long index = 111L;

        given(staffService.findByEmail(email)).willReturn(staff);
        doNothing().when(mailSendService).verificationMailSend(email,1, index,code);
        doNothing().when(staffService).emailVerifyCode(code, email);
        doNothing().when(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/mail/send")
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email,1, index,code);//code가 다름(랜덤)
//        verify(userService).emailVerifyCode(code, email);//code가 다름(랜덤)
        verify(logService).log(false, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);//code가 다름(랜덤)
    }

//    @Test
    @WithMockUser
    @DisplayName("mailSend case3 test")
    void mailSendCase3Test() throws Exception{

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        String email = "staff1234@gmail.com";
        String code = "testCode";//MailSendService.createKey();
        Long index = 33L;

        doNothing().when(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증, user not found", email,null,null,null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/mail/send")
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email,1, index,code);//code가 다름(랜덤)
//        verify(userService).emailVerifyCode(code, email);//code가 다름(랜덤)
        verify(logService).log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증, user not found", email,null,null,null);//code가 다름(랜덤)
    }

    @Test
    @WithMockUser
    @DisplayName("confirmEmail test")
    void confirmEmailTest() throws Exception{

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        String email = "staff1234@gmail.com";
        String code = "testCode";//MailSendService.createKey();

        //case1
        LocalDateTime curTime = LocalDateTime.now().minusMinutes(10);//41분전 가입 신청(30분 이내)
        user.setEmailVerifyStartTime(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email)).willReturn(user);
        given(staffService.findByEmail(email)).willReturn(null);
        doNothing().when(userService).emailCheck(email);

        String getUrl = String.format("/api/v1/mail/confirm/%s/%s", email, code);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        verify(userService).emailCheck(email);

        //case2
        curTime = LocalDateTime.now().minusMinutes(40);//40분전 가입 시간(30분)이 지난 case
        user.setEmailVerifyStartTime(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email)).willReturn(user);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        //case3
        curTime = LocalDateTime.now().plusYears(1);//인증 유효기간(1년)
        user.setWillConfirmDate(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email)).willReturn(user);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        //****** Staff Case
        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        String email2 = "staff1234@gmail.com";
        String code2 = "testCode";//MailSendService.createKey();

        //case1
        LocalDateTime curTime2 = LocalDateTime.now().minusMinutes(10);//41분전 가입 신청(30분 이내)
        staff.setEmailVerifyStartTime(curTime2);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email2)).willReturn(null);
        given(staffService.findByEmail(email2)).willReturn(staff);
        doNothing().when(staffService).emailCheck(email2);

        String getUrl2 = String.format("/api/v1/mail/confirm/%s/%s", email2, code2);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl2)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        verify(staffService).emailCheck(email2);

        //case2
        curTime = LocalDateTime.now().minusMinutes(40);//40분전 가입 시간(30분)이 지난 case
        staff.setEmailVerifyStartTime(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.findByEmail(email2)).willReturn(staff);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl2)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        //case3
        curTime = LocalDateTime.now().plusYears(1);//인증 유효기간(1년)
        staff.setWillConfirmDate(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.findByEmail(email2)).willReturn(staff);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl2)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        given(userService.findByEmail(email2)).willReturn(null);
        given(staffService.findByEmail(email2)).willReturn(null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl2)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());
    }
//    @Test
    @WithMockUser
    @DisplayName("confirmEmail staff test")
    void confirmEmailStaffTest() throws Exception{

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        String email = "staff1234@gmail.com";
        String code = "testCode";//MailSendService.createKey();

        //case1
        LocalDateTime curTime = LocalDateTime.now().minusMinutes(10);//41분전 가입 신청(30분 이내)
        staff.setEmailVerifyStartTime(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email)).willReturn(null);
        given(staffService.findByEmail(email)).willReturn(staff);
        doNothing().when(staffService).emailCheck(email);

        String getUrl = String.format("/api/v1/mail/confirm/%s/%s", email, code);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        verify(staffService).emailCheck(email);

        //case2
        curTime = LocalDateTime.now().minusMinutes(40);//40분전 가입 시간(30분)이 지난 case
        staff.setEmailVerifyStartTime(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.findByEmail(email)).willReturn(staff);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        //case3
        curTime = LocalDateTime.now().plusYears(1);//인증 유효기간(1년)
        staff.setWillConfirmDate(curTime);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(staffService.findByEmail(email)).willReturn(staff);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        given(userService.findByEmail(email)).willReturn(null);
        given(staffService.findByEmail(email)).willReturn(null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("checkMail test")
    void checkMailTest() throws Exception {
        Long idx = 33L;
        String email = "staff1234@gmail.com";

        String error = "null";
        given(staffService.checkLink(email, idx)).willReturn(error);

        String getUrl = String.format("/api/v1/mail/check-mail/%s/%s", idx, email);
        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        verify(staffService).checkLink(email, idx);
    }

    @Test
    @WithMockUser
    @DisplayName("passwordLink test")
    void passwordLinkTest() throws Exception {
        Long idx = 33L;
        String email = "staff1234@gmail.com";
        String key = "testkey";

        //case1
        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(userService.findByEmail(email)).willReturn(user);

        doNothing().when(mailSendService).verificationMailSend(email, 2, user.getIdx(), key);

        String getUrl = String.format("/api/v1/mail/link");
        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(mailSendService).verificationMailSend(email, 2, user.getIdx(), key);//key가 다르다.

        //case2
        given(userService.findByEmail(email)).willReturn(null);
//        doNothing().when(mailSendService).verificationMailSend(email, 2, user.getIdx(), key, false);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());

        //case3
        given(staffService.findByEmail(email)).willReturn(staff);
        doNothing().when(mailSendService).verificationMailSend(email, 2, user.getIdx(), key, false);

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("mailSendForInvite test")
    void mailSendForInviteTest() throws Exception {
        String email = "user1234@gmail.com";
        String staffEmail = "staff1234@gmail.com";
        String key = "testkey";

        //case1
        given(userService.findByEmail(email)).willReturn(user);
        doNothing().when(mailSendService).verificationMailSend(email, 4, 0L, key);
        given(staffService.findByEmail(staffEmail)).willReturn(staff);

        String getUrl = String.format("/api/v1/mail/invite");
        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .param("staffIdx", "111")
                        .param("staffEmail", staffEmail)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

        //case2
        given(userService.findByEmail(email)).willReturn(user);
        doNothing().when(mailSendService).verificationMailSend(email, 4, 0L, key);
        given(staffService.findByEmail(staffEmail)).willReturn(null);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .param("staffIdx", "111")
                        .param("staffEmail", staffEmail)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isLengthRequired())
                .andDo(print());

        //case3
        given(userService.findByEmail(email)).willReturn(null);
        doNothing().when(mailSendService).verificationMailSend(email, 4, 0L, key);
        given(staffService.findByEmail(staffEmail)).willReturn(staff);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .param("staffIdx", "111")
                        .param("staffEmail", staffEmail)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());

        //case4
        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        staffEmail = null;
        mockMvc.perform(MockMvcRequestBuilders.put(getUrl)
                        .param("staffIdx", "111")
                        .param("staffEmail", staffEmail)
                        .param("email", email)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isLengthRequired())
                .andDo(print());

    }
}