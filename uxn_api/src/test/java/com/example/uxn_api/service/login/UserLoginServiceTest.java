package com.example.uxn_api.service.login;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffActivityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserActivity;
import com.example.uxn_common.global.domain.user.repository.UserActivityRepository;
import com.example.uxn_common.global.domain.user.repository.UserAuthorityRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class UserLoginServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserAuthorityRepository userAuthorityRepository = Mockito.mock(UserAuthorityRepository.class);
    private final StaffRepository staffRepository = Mockito.mock(StaffRepository.class);

    private final LoginAttemptService loginAttemptService = Mockito.mock(LoginAttemptService.class);

    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final MailSendService mailSendService = Mockito.mock(MailSendService.class);

    private final UserActivityRepository userActivityRepository = Mockito.mock(UserActivityRepository.class);
    private final StaffActivityRepository staffActivityRepository = Mockito.mock(StaffActivityRepository.class);

    private UserLoginService userLoginService;

    private Staff staff;
    private User user;

    @BeforeEach
    public void setUpTest() {
        userLoginService = new UserLoginService(userRepository, userAuthorityRepository, staffRepository, loginAttemptService
        ,request, mailSendService, userActivityRepository, staffActivityRepository);

        staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .emailVerifyStartTime(LocalDateTime.now())
                .role(null)
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername test")
    void loadUserByUsernameTest() {
        String ip = "192.168.0.14";

        // given
        Mockito.when(loginAttemptService.isBlocked(user.getEmail(), ip)).thenReturn(false);
        Mockito.when(userActivityRepository.save(any(UserActivity.class))).then(returnsFirstArg());

        Mockito.when(staffRepository.findByEmail(user.getEmail())).thenReturn(staff);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        //Case1 - 인증 메일 시간은 정상, 인증을 안한 상태
        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Case2 - 인증 메일 시간 30분 초과 상태
        // when
        try {
            user.setEmailVerifyStartTime(LocalDateTime.now().minusMinutes(40));
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //case3 - 회원 유효기간 종료 상태
        user.setWillConfirmDate(LocalDateTime.now().minusYears(2));
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //case4 - 인증 성공 상태 -> 로그인 성공 상태
        user.setWillConfirmDate(LocalDateTime.now().plusYears(1));
        user.setEmailVerifiedSuccess(true);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        user.setWillConfirmDate(LocalDateTime.now().minusYears(2));
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        user.setWillConfirmDate(null);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Staff Case
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        staff.setEmailVerifyStartTime(LocalDateTime.now());
        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(staff);

        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        staff.setEmailVerifyStartTime(LocalDateTime.now().minusMinutes(40));
        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(staff);

        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        staff.setEmailVerifyStartTime(LocalDateTime.now().minusMinutes(10));
        staff.setEmailVerifiedSuccess(true);
        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(staff);

        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        staff.setWillConfirmDate(LocalDateTime.now().plusYears(1));
        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(staff);

        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        staff.setWillConfirmDate(LocalDateTime.now().minusMinutes(10));
        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(staff);

        // when
        try {
            userLoginService.loadUserByUsername(user.getEmail());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("loginVerify test")
    void loginVerifyTest() {

        // given
        Mockito.when(staffRepository.findByEmail(user.getEmail())).thenReturn(staff);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        LoginReqDto rto = LoginReqDto.builder()
                .userId(user.getEmail())
                .password(user.getPassword())
                .build();
        // when
        userLoginService.loginVerify(rto);
    }

    @Test
    @DisplayName("doWhenLoginSuccess test")
    void doWhenLoginSuccessTest() {
        String ip = "192.168.0.14";
        String email = "test123@gmail.com";
        String sessionId = "sessionId";

        // given
        Mockito.doNothing().when(loginAttemptService).loginSucceeded(ip, email);
        Mockito.when(userActivityRepository.save(any(UserActivity.class)))
                .then(returnsFirstArg());

        // when
        userLoginService.loginAttempt(sessionId, email, ip);
        userLoginService.doWhenLoginSuccess(ip, email, sessionId);

//        verify(userActivityRepository).save(any(UserActivity.class));

        int a = 0;
    }

    @Test
    @DisplayName("doWhenLoginFailCase1 test")
    void doWhenLoginFailCase1Test() {
        String sessionId = "sessionId";
        String email = "test123@gmail.com";
        String ip = "192.168.0.14";
        int status = 410;

        userLoginService.loginAttempt(sessionId, email, ip);

        // given
        Mockito.when(userActivityRepository.save(any(UserActivity.class)))
                .then(returnsFirstArg());
        // when
        userLoginService.loginAttempt(sessionId, email, ip);
        userLoginService.doWhenLoginFail(ip, status, sessionId);

        status = 400;
        userLoginService.doWhenLoginFail(ip, status, sessionId);
    }

    @Test
    @DisplayName("doWhenLoginFailCase2 test")
    void doWhenLoginFailCase2Test() {
        String sessionId = "sessionId";
        String email = "test123@gmail.com";
        String ip = "192.168.0.14";

        // given
//        String tmp = mSessionIdMap.get(sessionId);
//        Mockito.doNothing().when(loginFailed).loginSucceeded(ip, email);
        Mockito.when(userActivityRepository.save(any(UserActivity.class)))
                .then(returnsFirstArg());

        // when
        userLoginService.loginAttempt(sessionId, email, ip);
        userLoginService.doWhenLoginFail(ip, sessionId);

    }

    @Test
    @DisplayName("loginAttempt test")
    void loginAttemptTest() {
        String sessionId = "sessionId";
        String email = "test123@gmail.com";
        String ip = "192.168.0.14";
        // given
//        Mockito.when(userActivityRepository.save(user.getEmail())).thenReturn(staff);
//        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // when
        userLoginService.loginAttempt(sessionId, email, ip);
    }
}