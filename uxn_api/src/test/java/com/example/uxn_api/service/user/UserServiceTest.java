package com.example.uxn_api.service.user;

import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.login.LoginAttemptService;
import com.example.uxn_api.service.login.TokenService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ChangePasswordToken;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.example.uxn_common.global.domain.user.UserStaffMapping;
import com.example.uxn_common.global.domain.user.repository.ChangePasswordTokenRepository;
import com.example.uxn_common.global.domain.user.repository.UserAuthorityRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.user.repository.UserStaffMappingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final StaffRepository staffRepository = Mockito.mock(StaffRepository.class);
    private final DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);
    private final UserStaffMappingRepository userStaffMappingRepository = Mockito.mock(UserStaffMappingRepository.class);
    private final NoteRepository noteRepository = Mockito.mock(NoteRepository.class);
    private final CalibrationRepository calibrationRepository = Mockito.mock(CalibrationRepository.class);
    private final UserAuthorityRepository userAuthorityRepository = Mockito.mock(UserAuthorityRepository.class);
    private final LoginAttemptService loginAttemptService = Mockito.mock(LoginAttemptService.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final ChangePasswordTokenRepository changePasswordTokenRepository = Mockito.mock(ChangePasswordTokenRepository.class);
    private final TokenService tokenService = Mockito.mock(TokenService.class);
    private final LogService logService = Mockito.mock(LogService.class);
    private UserService userService;
    private Staff staff;
    private User user;

    @BeforeEach
    public void setUpTest() {
        userService = new UserService(userRepository, staffRepository, deviceRepository
                , userStaffMappingRepository, noteRepository, calibrationRepository
                , userAuthorityRepository, loginAttemptService, request
                , changePasswordTokenRepository, tokenService, logService);

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
    @DisplayName("deleteUser test")
    void deleteUserTest() {
        // given
        Mockito.doNothing().when(deviceRepository).deleteAllByUser(user);
        Mockito.doNothing().when(userStaffMappingRepository).deleteAllByStaffId(staff.getIdx());
        Mockito.doNothing().when(noteRepository).deleteAllByUser(user);
        Mockito.doNothing().when(calibrationRepository).deleteAllByUser(user);
        Mockito.doNothing().when(userAuthorityRepository).deleteAll(user.getAuthorities());

        // when
        userService.deleteUser(user);

        // then
        Assertions.assertEquals(user.getUsername(), "user");

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("addAuthority test")
    void addAuthorityTest() {
        //case1
        Long id = 111L;
        String authority = null;//"ROLE_USER";

        // given
        Mockito.when(userRepository.save(any(User.class))).then(returnsFirstArg());
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // when
        userService.addAuthority(id, authority);

        // then
        Assertions.assertEquals(user.getUsername(), "user");
        verify(userRepository).save(user);

        //case2

        // given
        UserAuthority userAuthority = new UserAuthority(id, "ROLE_USER");
        Set<UserAuthority> userAuthority2 = new HashSet<>();
        userAuthority2.add(userAuthority);

        user.setAuthorities(userAuthority2);
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(any(User.class))).then(returnsFirstArg());

        // when
        userService.addAuthority(id, authority);

        // then
        Assertions.assertEquals(user.getUsername(), "user");
//        verify(staffRepository).save(staff);
    }

    @Test
    @DisplayName("saveUser test")
    void saveUserTest() {
        // given
        Mockito.when(userRepository.save(any(User.class)))
                .then(returnsFirstArg());

        User user2 = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        // when
        User user = userService.saveUser(user2);

        // then
        Assertions.assertEquals(user.getUsername(), "user");
        Assertions.assertEquals(user.getEmail(), "user123@gmail.com");

        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("emailVerifyCode test")
    void emailVerifyCodeTest() {
        String code = "verifyCode";
        String email = "user123@gmail.com";

        // given
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(user);

        // when
        userService.emailVerifyCode(code, email);

        verify(userRepository).findByEmail(any());
    }

    @Test
    @DisplayName("emailCheck test")
    void emailCheckTest() {
        String email = "user123@gmail.com";

        // given
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(user);

        // when
        userService.emailCheck(email);

        verify(userRepository).findByEmail(any());

    }

    @Test
    @DisplayName("updatePassword test")
    void updatePasswordTest() {
        String email = "test123@gmail.com";
        String password = "test123";
        String token = "token";

        ChangePasswordToken changePasswordToken = ChangePasswordToken.builder()
                .email(email)
                .token(token)
                .build();

        // given
        Mockito.when(changePasswordTokenRepository.findTopByTokenOrderByCreateTimeDesc(token))
                .thenReturn(changePasswordToken);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        Mockito.doNothing().when(loginAttemptService).updateBlocked("key", email);
        Mockito.doNothing().when(tokenService).deleteAllToken(email);

        // when
        userService.updatePassword(email, password, token);

        verify(tokenService).deleteAllToken(any());

        //case2 - user & staff null
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);
        userService.updatePassword(email, password, token);

        //case3 - user null
        Mockito.when(staffRepository.findByEmail(email)).thenReturn(staff);
        userService.updatePassword(email, password, token);

        //case4 - email null
        try {
            userService.updatePassword(null, password, token);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //case5 - token null
        Mockito.when(changePasswordTokenRepository.findTopByTokenOrderByCreateTimeDesc(token))
                .thenReturn(null);

        try {
            userService.updatePassword(email, password, token);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("withdrawal test")
    void withdrawalTest() {
        String email = "user123@gmail.com";

        // given
        Mockito.when(userRepository.findByEmail(email))
                .thenReturn(user);

        // when
        userService.withdrawal(email);

        verify(userRepository).findByEmail(any());
    }

    @Test
    @DisplayName("appendUserToStaff test")
    void appendUserToStaffTest() {
        Long userId = 33L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(1L);
        mapping.setUserId(user.getIdx());
        mapping.setStaffId(staff.getIdx());

        List<UserStaffMapping> mappingList = new ArrayList<>();
        mappingList.add(mapping);

        // given
        Mockito.when(userRepository.findByIdx(userId)).thenReturn(user);
        Mockito.when(userStaffMappingRepository.findAllByUserId(userId)).thenReturn(mappingList);
        Mockito.when(userStaffMappingRepository.save(mapping)).then(returnsFirstArg());

        // when
        userService.appendUserToStaff(user.getIdx(), staff.getIdx(), true);

        // then
//        Assertions.assertEquals(calibrationSaveResDto.getTitle(), "Calibration");
//        Assertions.assertEquals(calibrationSaveResDto.getContents(), "99");

        verify(userStaffMappingRepository).save(any());
    }
}