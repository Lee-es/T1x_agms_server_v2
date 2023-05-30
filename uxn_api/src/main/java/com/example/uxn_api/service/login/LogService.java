package com.example.uxn_api.service.login;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_common.global.domain.staff.StaffActivity;
import com.example.uxn_common.global.domain.staff.repository.StaffActivityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.patient.PatientActivity;
import com.example.uxn_common.global.domain.patient.repository.PatientActivityRepository;
import com.example.uxn_common.global.domain.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;


    private static HashMap<String, String> mSessionIdMap;

    private final MailSendService mailSendService;

    private final PatientActivityRepository patientActivityRepository;
    private final StaffActivityRepository staffActivityRepository;


    @Transactional
    public void log(boolean isUser, ActivityKind kind,String summary,  String subInfo1, String subInfo2, String subInfo3, String longText){
        Date n = new Date();
        String curTime = String.format("%tF %tT", n,n);
        LocalDateTime localDateTime = LocalDateTime.parse(curTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if(isUser){
            patientActivityRepository.save(PatientActivity.builder()
                    .summary(summary)
                    .activityKind(kind)
                    .subInfo1(subInfo1)
                    .subInfo2(subInfo2)
                    .subInfo3(subInfo3)
                    .text(longText)
                    .createTime(localDateTime)
                    .build());
        } else {
            staffActivityRepository.save(StaffActivity.builder()
                    .summary(summary)
                    .activityKind(kind)
                    .subInfo1(subInfo1)
                    .subInfo2(subInfo2)
                    .subInfo3(subInfo3)
                    .text(longText)
                    .createTime(localDateTime)
                    .build());
        }

    }
}
