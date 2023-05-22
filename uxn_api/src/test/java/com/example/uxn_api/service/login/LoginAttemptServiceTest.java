package com.example.uxn_api.service.login;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_common.global.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

class LoginAttemptServiceTest {
    private LoginAttemptService loginAttemptService;

    @BeforeEach
    public void setUpTest() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    @DisplayName("loginSucceeded test")
    void loginSucceededTest() {
        String key = "key";
        String id = "id";

//        // given
//        Mockito.when(userRepository.save(any(User.class))).then(returnsFirstArg());

        // when
        loginAttemptService.loginSucceeded(key, id);
    }

    @Test
    @DisplayName("loginFailed test")
    void loginFailedTest() {
        String key = "key";
        String id = "id";

        // when
        loginAttemptService.loginFailed(key, id);
    }

    @Test
    @DisplayName("isBlocked test")
    void isBlockedTest() {
        String key = "key";
        String id = "id";

        // when
        loginAttemptService.isBlocked(key, id);
    }

    @Test
    @DisplayName("updateBlocked test")
    void updateBlockedTest() {
        String key = "key";
        String id = "id";

        // when
        loginAttemptService.updateBlocked(key, id);
    }
}