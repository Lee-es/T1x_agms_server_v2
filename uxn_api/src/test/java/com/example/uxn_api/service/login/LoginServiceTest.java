package com.example.uxn_api.service.login;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.web.login.dto.res.LoginCheckResult;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.ChangePasswordTokenRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class LoginServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final StaffRepository staffRepository = Mockito.mock(StaffRepository.class);
    private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    private final  UserTokenRepository userTokenRepository = Mockito.mock(UserTokenRepository.class);
    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final MailSendService mailSendService = Mockito.mock(MailSendService.class);
    private final ChangePasswordTokenRepository changePasswordTokenRepository = Mockito.mock(ChangePasswordTokenRepository.class);
    private final LogService logService = Mockito.mock(LogService.class);

    private LoginService loginService;

    private Staff staff;
    private User user;

    @BeforeEach
    public void setUpTest() {
        loginService = new LoginService(userRepository, staffRepository, passwordEncoder
                , userTokenRepository, tokenService, mailSendService, changePasswordTokenRepository, logService);

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
                .role(null)
                .build();
    }

    @Test
    @DisplayName("userVerify test")
    void userVerifyTest() {
        String pwd = "uxn1234!@";
        // given
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(staffRepository.findByEmail(user.getEmail())).thenReturn(staff);

        //case1
        Mockito.when(passwordEncoder.matches(pwd, user.getPassword())).thenReturn(true);
        // when
        Login login = loginService.userVerify(user.getEmail(), pwd);

        //case2
//        Mockito.when(passwordEncoder.matches(pwd, user.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.matches(pwd, staff.getPassword())).thenReturn(true);
        user.setPassword("wrong pwd");
        // when
        login = loginService.userVerify(user.getEmail(), pwd);

        //case3
        Mockito.when(passwordEncoder.matches(pwd, user.getPassword())).thenReturn(false);
        Mockito.when(passwordEncoder.matches(pwd, staff.getPassword())).thenReturn(false);
        // when
        login = loginService.userVerify(user.getEmail(), pwd);


        // then
//        Assertions.assertEquals(user.getUsername(), "user");
//        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("confirmLogin test")
    void confirmLoginTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String device = "device";

        String email = "user123@gmail.com";
        String ip = "192.168.0.1";
        String refreshToken = "refreshToken";

        String token = "token";

        UsernamePasswordAuthenticationToken authenticationToken = Mockito.mock(UsernamePasswordAuthenticationToken.class);
//        authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

//        UsernamePasswordAuthenticationToken authenticationToken =
//                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();


        // given
        Mockito.when(tokenService.saveToken(email, device, ip, refreshToken)).thenReturn(token);
        Mockito.when(authenticationToken.getName()).thenReturn(email);

        // when
        LoginCheckResult loginCheckResult = loginService.confirmLogin(request, device);
    }

    @Test
    @DisplayName("checkLogin test")
    void checkLoginTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String device = "device";
        String saveToken = "saveToken";

        // when
        LoginCheckResult loginCheckResult = loginService.checkLogin(request, device, saveToken);

    }


}