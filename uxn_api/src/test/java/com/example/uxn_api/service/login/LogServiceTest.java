package com.example.uxn_api.service.login;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffActivity;
import com.example.uxn_common.global.domain.staff.repository.StaffActivityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.UserActivity;
import com.example.uxn_common.global.domain.user.repository.UserActivityRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class LogServiceTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private StaffRepository staffRepository = Mockito.mock(StaffRepository.class);
    private MailSendService mailSendService = Mockito.mock(MailSendService.class);
    private UserActivityRepository userActivityRepository = Mockito.mock(UserActivityRepository.class);
    private StaffActivityRepository staffActivityRepository = Mockito.mock(StaffActivityRepository.class);

    private LogService logService;

    @BeforeEach
    public void setUpTest() {
        logService = new LogService(userRepository, staffRepository, mailSendService
                , userActivityRepository, staffActivityRepository);
    }

    @Test
    @DisplayName("log test")
    void logTest() {

        String email = "test123@gmailcom";
        // given
        Mockito.when(userActivityRepository.save(any(UserActivity.class)))
                .then(returnsFirstArg());

        Mockito.when(staffActivityRepository.save(any(StaffActivity.class)))
                .then(returnsFirstArg());

        // when
        logService.log(true, ActivityKind.FIND_PASSWORD,"비밀번호 재설정 링크 전송", email,null,null,null);
        logService.log(false, ActivityKind.FIND_PASSWORD,"비밀번호 재설정 링크 전송", email,null,null,null);


        // then
//        Assertions.assertEquals(staff.getStaffName(), "staff");
        verify(userActivityRepository).save(any(UserActivity.class));
        verify(staffActivityRepository).save(any(StaffActivity.class));
    }
}