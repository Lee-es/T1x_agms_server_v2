package com.example.uxn_api.service.email;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.mail.MessagingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class MailSendServiceTest {

    private MailSendService mailSendService;

    @BeforeEach
    public void setUpTest() {
        mailSendService = new MailSendService();

    }

    @Test
    void verificationMailSend() {
        String recipient = "test@uxn.co.kr";//메일 수신 주소
        int num = 1;
        Long idx = 33L;
        String currentCode = "code";

        // given
//        Mockito.when(MailSendService.findById(userID))
//                .thenReturn(Optional.of(calibration));

//        given(userService.findByEmail(email)).willReturn(user);

        //메일 송신 계정 정보
        mailSendService.setUser("commoncoding.myeongjeong.chae@gmail.com");
        mailSendService.setPassword("poertxbakxvyhamo");
//
        // when
        try {
            mailSendService.verificationMailSend(recipient, num, idx, currentCode);

        }catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        // then
//        Assertions.assertEquals(calibrationDetailResDto.getTitle(), calibration.getTitle());
//        Assertions.assertEquals(calibrationDetailResDto.getContents(), calibration.getContents());
//
//        verify(calibrationRepository).findById(userID);
    }

    @Test
    void createKey() {
        String key = mailSendService.createKey();
    }
}