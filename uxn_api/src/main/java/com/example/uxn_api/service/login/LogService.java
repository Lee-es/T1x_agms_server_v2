package com.example.uxn_api.service.login;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffActivity;
import com.example.uxn_common.global.domain.staff.repository.StaffActivityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserActivity;
import com.example.uxn_common.global.domain.user.repository.UserActivityRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService  {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;


    private static HashMap<String, String> mSessionIdMap;

    private final MailSendService mailSendService;

    private final UserActivityRepository userActivityRepository;
    private final StaffActivityRepository staffActivityRepository;


    @Transactional
    public void log(boolean isUser, ActivityKind kind,String summary,  String subInfo1, String subInfo2, String subInfo3, String longText){
        if(isUser){
            userActivityRepository.save(UserActivity.builder()
                    .summary(summary)
                    .activityKind(kind)
                    .subInfo1(subInfo1)
                    .subInfo2(subInfo2)
                    .subInfo3(subInfo3)
                    .text(longText)
                    .createTime(LocalDateTime.now())
                    .build());
        } else {
            staffActivityRepository.save(StaffActivity.builder()
                    .summary(summary)
                    .activityKind(kind)
                    .subInfo1(subInfo1)
                    .subInfo2(subInfo2)
                    .subInfo3(subInfo3)
                    .text(longText)
                    .createTime(LocalDateTime.now())
                    .build());
        }

    }
}
