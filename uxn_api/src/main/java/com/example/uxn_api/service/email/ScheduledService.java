package com.example.uxn_api.service.email;

//import com.example.uxn_common.global.domain.staff.Staff;
//import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
//import com.example.uxn_common.global.domain.user.User;
//import com.example.uxn_common.global.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledService {

//    private final UserRepository userRepository;
//    private final StaffRepository staffRepository;
//
//    private final MailSendService mailSendService;
//
//
//    @Transactional
//    protected void saveUser(User user){
//        if(user==null){
//            log.debug("empty user");
//            return;
//        }
//        userRepository.save(user);
//    }
//
//    public void doCheckUsers() throws MessagingException {
//        List< Staff> allStaff = staffRepository.findAll();
//        for(Staff staff : allStaff){
//            if(staff.getWillConfirmDate()!=null){
//                if(staff.getWillConfirmDate().plusDays(14).isBefore(LocalDateTime.now())){
//                    if(staff.getEmailVerifyStartTime()!=null && staff.getEmailVerifyStartTime().plusDays(1).isBefore(LocalDateTime.now())){
//                        Long index = staff.getIdx();
//                        String code = MailSendService.createKey();
//                        mailSendService.verificationMailSend(staff.getEmail(),1,index,code);
//                        staff.setEmailVerifyCode(code);
//                        staff.setEmailVerifyStartTime(LocalDateTime.now());
//                        staffRepository.save(staff);
//                    }
//
//
//                }
//            }
//        }
//        List<User> allUser = userRepository.findAll();
//        for(User user : allUser){
//            if(user.getWillConfirmDate()!=null){
//                if(user.getWillConfirmDate().isBefore(LocalDateTime.now())){
//                    if(user.getEmailVerifyStartTime()!=null && user.getEmailVerifyStartTime().plusDays(1).isBefore(LocalDateTime.now())){
//                        String email = user.getEmail();
//
//                        Long index = user.getIdx();
//                        try {
//                            String code = MailSendService.createKey();
//                            mailSendService.verificationMailSend(email,1, index,code);
//                            user.setEmailVerifyCode(code);
//                            user.setEmailVerifyStartTime(LocalDateTime.now());
//                            userRepository.save(user);
//                        } catch (MessagingException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                    }
//
//
//                }
//            }
//        }
//    }
//
//
////    @Scheduled(cron = "0 0 4 * * *")
//    public void checkEvery5minutes() throws MessagingException {
//        doCheckUsers();
//    }


}
