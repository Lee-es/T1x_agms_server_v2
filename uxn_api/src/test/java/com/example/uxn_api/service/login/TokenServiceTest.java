package com.example.uxn_api.service.login;

import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.user.UserToken;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class TokenServiceTest {
    private UserTokenRepository userTokenRepository = Mockito.mock(UserTokenRepository.class);
    private TokenService tokenService;

    @BeforeEach
    public void setUpTest() {
        tokenService = new TokenService(userTokenRepository);

    }

    @Test
    @DisplayName("saveToken test")
    void saveToken() {
        String email = "user123@gmail.com";
        String device = "device";
        String ip = "192.168.0.1";
        String refreshToken = "refreshToken";

        UserToken userToken = UserToken.builder()
                .email(email)
                .device(device)
                .ip(ip)
                .refreshToken(refreshToken)
                .build();

        List<UserToken> userTokenList = new ArrayList<>();
//        userTokenList.set(0, userToken);
        userTokenList.add(userToken);

        // given
        Mockito.when(userTokenRepository.findAllByEmailAndDevice(email, device))
                .thenReturn(userTokenList);

        // when
        String token = tokenService.saveToken(email, device, ip, refreshToken);

        // then
        Assertions.assertNotNull(token);

        verify(userTokenRepository).save(any());
    }

    @Test
    @DisplayName("updateExpireTime test")
    void updateExpireTime() {
        String email = "user123@gmail.com";
        String device = "device";
        String ip = "192.168.0.1";
        String token = "token";
        String refreshToken = "refreshToken";

        LocalDateTime time = LocalDateTime.now().plusMinutes(10);

        UserToken userToken = UserToken.builder()
                .email(email)
                .device(device)
                .ip(ip)
                .token(token)
                .refreshToken(refreshToken)
                .expireTime(time)
                .build();

        List<UserToken> userTokenList = new ArrayList<>();
//        userTokenList.set(0, userToken);
        userTokenList.add(userToken);

        // given
        Mockito.when(userTokenRepository.findAllByEmailAndToken(email, token))
                .thenReturn(userTokenList);

        // when
        tokenService.updateExpireTime(email, token, refreshToken);

        // then
//        Assertions.assertNotNull(userTokenList.get(0).getExpireTime());

        verify(userTokenRepository).findAllByEmailAndToken(email,token);
    }
}