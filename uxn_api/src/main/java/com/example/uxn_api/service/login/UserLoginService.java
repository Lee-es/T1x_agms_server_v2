package com.example.uxn_api.service.login;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.login.dto.req.LoginDto;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffActivityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserActivity;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.example.uxn_common.global.domain.user.repository.UserActivityRepository;
import com.example.uxn_common.global.domain.user.repository.UserAuthorityRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    private final StaffRepository staffRepository;

    private final LoginAttemptService loginAttemptService;

    private final HttpServletRequest request;

    private static HashMap<String, String> mSessionIdMap;

    private final MailSendService mailSendService;

    private final UserActivityRepository userActivityRepository;
    private final StaffActivityRepository staffActivityRepository;

    private String getClientIP() {

        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }



    @Transactional
    public UserAuthority findUserAuthorityByUserID(Long id){
        return userAuthorityRepository.findByIdx(id);
    }

    @Transactional
    public String findEmailByUserEmail(String email){
        return userRepository.getUserEmailByUserEmail(email);
    }
    public Long findUserIDByUserEmail(String email){
        return userRepository.getUserIdByUserEmail(email);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException { // 파라미터 값으로는 UNIQUE(나는 id) 한 값을 받는다. 동작하는 시점은 로그인 할때.
        // return 타입을 분기해서 타입에 맞게 리턴 시켜주면 굳이 멀티모듈을 사용하거나 따로따로 구현 할 필요 없음. -> if 문으로 분기해서 리턴 해주거나 담당 Class 를 따로 만들어서 return
        log.debug("loadUserByUsername called:"+userId);
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip,userId)) {
//            throw new RuntimeException("blocked");
            log.debug("user blocked");
            userActivityRepository.save(UserActivity.builder()
                    .summary("로그인 실패:" + userId + " / " + ip)
                    .activityKind(ActivityKind.LOGIN_FAIL)
                    .createTime(LocalDateTime.now())
                    .subInfo1("user block")
                    .build());
            throw new LoginException(String.valueOf(ErrorCode.BLOCKED_USER.getStatus()),ErrorCode.BLOCKED_USER);
        }


        Staff staff = staffRepository.findByEmail(userId);
        User user = userRepository.findByEmail(userId);


        if(staff != null && staff.isEmailVerifiedSuccess()) {
            log.info("로그인 성공 43");
            if(staff.getWillConfirmDate()!=null){
                if(staff.getWillConfirmDate().isAfter(LocalDateTime.now())){
                    log.info("로그인 성공 46");
                    return staff;
                } else {
                    //1년 초과된 시간
                    throw new LoginException("회원 유효기간 종료", ErrorCode.OVER_YEAR);
                }
            } else {
                //회원 가입이 정상적이지 않는 상태
                userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 실패:" + userId + " / " + ip)
                        .activityKind(ActivityKind.LOGIN_FAIL)
                        .createTime(LocalDateTime.now())
                        .subInfo1("회원 가입이 정상적이지 않는 상태")
                        .build());
                throw new LoginException("회원 가입이 정상적이지 않는 상태", ErrorCode.NOT_COMPLETE_SIGN_UP);
            }
        } else if (user != null && user.isEmailVerifiedSuccess()) {
            //회원 가입

            if(user.getWillConfirmDate()!=null){
                if(user.getWillConfirmDate().isAfter(LocalDateTime.now())){
                    log.info("로그인 성공 63");
                    return user;
                } else {
                    //1년 초과된 시간,
                    log.info("로그인 1년 초과된 시간,");

                    throw new LoginException("회원 유효기간 종료", ErrorCode.OVER_YEAR);


                }
            } else {
                //회원 가입이 정상적이지 않는 상태
                userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 실패:" + userId + " / " + ip)
                        .activityKind(ActivityKind.LOGIN_FAIL)
                        .createTime(LocalDateTime.now())
                        .subInfo1("회원 가입이 정상적이지 않는 상태")
                        .build());
                throw new LoginException("회원 가입이 정상적이지 않는 상태", ErrorCode.NOT_COMPLETE_SIGN_UP);
            }

        } else if(user != null && !user.isEmailVerifiedSuccess()) {
            log.info("이메일 인증 에러");
            if(user.getWillConfirmDate() == null){
                LocalDateTime now = LocalDateTime.now();
                log.info("now : "+now.toString());
                if(user.getEmailVerifyStartTime().plusMinutes(30).isBefore(now)){
                    log.info("email verify time out!");
                    //인증 메일 시간 초과, 다시 로그인을 하라고 알림
                    userActivityRepository.save(UserActivity.builder()
                            .summary("로그인 실패:" + userId + " / " + ip)
                            .activityKind(ActivityKind.LOGIN_FAIL)
                            .createTime(LocalDateTime.now())
                            .subInfo1("인증 메일 시간 초과, 다시 회원가입을 하라고 알림")
                            .build());
                    throw new LoginException("인증 메일 시간 초과, 다시 회원가입을 하라고 알림", ErrorCode.OVER_VERIFY_TIME_30);
                } else {
                    //인증 메일 시간은 정상, 인증을 안한상태라고 알림
                    userActivityRepository.save(UserActivity.builder()
                            .summary("로그인 실패:" + userId + " / " + ip)
                            .activityKind(ActivityKind.LOGIN_FAIL)
                            .createTime(LocalDateTime.now())
                            .subInfo1("인증 메일 시간은 정상, 인증을 안한 상태")
                            .build());
                    throw new LoginException("인증 메일 시간은 정상, 인증을 안한 상태라고 알림", ErrorCode.NOT_COMPLETE_EMAIL_VERIFY);
                }

            } else {
                //1년 초과된 시간,
                userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 실패:" + userId + " / " + ip)
                        .activityKind(ActivityKind.LOGIN_FAIL)
                        .createTime(LocalDateTime.now())
                        .subInfo1("회원 유효기간 종료")
                        .build());
                throw new LoginException("회원 유효기간 종료", ErrorCode.OVER_YEAR);
            }
        }else if(staff != null && !staff.isEmailVerifiedSuccess()) {
            log.info("이메일 인증 에러");

            if(staff.getWillConfirmDate() == null){
                LocalDateTime now = LocalDateTime.now();
                log.info("now : "+now.toString());
                if(staff.getEmailVerifyStartTime().plusMinutes(30).isBefore(now)){
                    log.info("email verify time out!");
                    //인증 메일 시간 초과, 다시 로그인을 하라고 알림
                    userActivityRepository.save(UserActivity.builder()
                            .summary("로그인 실패:" + userId + " / " + ip)
                            .activityKind(ActivityKind.LOGIN_FAIL)
                            .createTime(LocalDateTime.now())
                            .subInfo1("인증 메일 시간 초과")
                            .build());
                    throw new LoginException("인증 메일 시간 초과, 다시 로그인을 하라고 알림", ErrorCode.OVER_VERIFY_TIME_30);
                } else {
                    userActivityRepository.save(UserActivity.builder()
                            .summary("로그인 실패:" + userId + " / " + ip)
                            .activityKind(ActivityKind.LOGIN_FAIL)
                            .createTime(LocalDateTime.now())
                            .subInfo1("인증 메일 시간은 정상, 인증을 안한상태")
                            .build());
                    //인증 메일 시간은 정상, 인증을 안한상태라고 알림
                    throw new LoginException("인증 메일 시간은 정상, 인증을 안한상태라고 알림", ErrorCode.NOT_COMPLETE_EMAIL_VERIFY);
                }

            } else {
                //1년 초과된 시간,
                userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 실패:" + userId + " / " + ip)
                        .activityKind(ActivityKind.LOGIN_FAIL)
                        .createTime(LocalDateTime.now())
                        .subInfo1("회원 유효기간 종료")
                        .build());
                throw new LoginException("회원 유효기간 종료", ErrorCode.OVER_YEAR);
            }
        }else {
            log.info("Login failed");
            userActivityRepository.save(UserActivity.builder()
                    .summary("로그인 실패:" + userId + " / " + ip)
                    .activityKind(ActivityKind.LOGIN_FAIL)
                    .createTime(LocalDateTime.now())
                    .subInfo1("회원 정보 없음")
                    .build());
            return null;
        }

//        if(user != null) {
//            return user;
//        }else if(staff != null) {
//            return staff;
//        }else {
//            return null;
//        }

    }

    public Login loginVerify(LoginReqDto dto) {
        log.debug("loginVerify 129");
        User user = userRepository.findByEmailAndPassword(dto.getUserId(), dto.getPassword());
        Staff staff = staffRepository.findByEmailAndPassword(dto.getUserId(), dto.getPassword());
        if(user != null) {
            return user;
        }else {
            return staff;
        }
    }

    @Transactional
    public void doWhenLoginSuccess(String ip, String email, String sessionId){
        loginAttemptService.loginSucceeded(ip,email);
        if(sessionId!=null){
            log.debug("doWhenLoginSuccess "+sessionId);
            mSessionIdMap.remove(sessionId);
        }
        userActivityRepository.save(UserActivity.builder()
                .summary("로그인 성공:" + email + " / " + ip)
                .activityKind(ActivityKind.LOGIN_SUCCESS)
                .createTime(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void doWhenLoginFail(String ip, int status, String sessionId){

        if(sessionId!=null){
            log.debug("loginAttempt "+sessionId + " / " + status);
            String tmp = mSessionIdMap.get(sessionId);
            mSessionIdMap.remove(sessionId);
            if(status == 410){
                doWhen410Error(tmp, ip);
            } else {
                userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 실패 " + tmp + " / " + ip)
                        .activityKind(ActivityKind.LOGIN_FAIL)
                        .createTime(LocalDateTime.now())
                        .build());
            }
        }

    }
    public void doWhenLoginFail(String ip, String sessionId){
        if(sessionId!=null){

            String tmp = mSessionIdMap.get(sessionId);
            loginAttemptService.loginFailed(ip,tmp);
            mSessionIdMap.remove(sessionId);
            userActivityRepository.save(UserActivity.builder()
                    .summary("로그인 실패 id,비밀번호 불일치:" +  ip)
                    .activityKind(ActivityKind.LOGIN_FAIL)
                    .createTime(LocalDateTime.now())
                    .build());
        }

    }

    @Transactional
    public void loginAttempt(String sessionId, String email, String ip){
        if(mSessionIdMap==null){
            mSessionIdMap = new HashMap<>();
        }
        if(sessionId!=null){
            log.debug("loginAttempt "+sessionId);
            mSessionIdMap.put(sessionId,email);
        }
        userActivityRepository.save(UserActivity.builder()
                        .summary("로그인 시도:" + email + " / " + ip)
                        .activityKind(ActivityKind.TRY_LOGIN)
                        .createTime(LocalDateTime.now())
                .build());
    }

    private void doWhen410Error(String email, String ip){
        User user = userRepository.findByEmail(email);
        Staff staff = staffRepository.findByEmail(email);
        if(user!=null){
            Long index = user.getIdx();
            try {

                String code = MailSendService.createKey();
                mailSendService.verificationMailSend(user.getEmail(),1,index,code);


                user.setEmailVerifyCode(code);
                user.setEmailVerifyStartTime(LocalDateTime.now());
                userRepository.save(user);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            userActivityRepository.save(UserActivity.builder()
                    .summary("로그인 실패:" + email + " / " + ip)
                    .activityKind(ActivityKind.LOGIN_FAIL)
                    .createTime(LocalDateTime.now())
                    .subInfo1("회원 유효기간 종료")
                    .build());
        } else if(staff !=null){
            Long index = staff.getIdx();
            try {
                String code = MailSendService.createKey();
                mailSendService.verificationMailSend(staff.getEmail(),1,index,code);
                staff.setEmailVerifyCode(code);
                staff.setEmailVerifyStartTime(LocalDateTime.now());
                staffRepository.save(staff);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            userActivityRepository.save(UserActivity.builder()
                    .summary("로그인 실패:" + email + " / " + ip)
                    .activityKind(ActivityKind.LOGIN_FAIL)
                    .createTime(LocalDateTime.now())
                    .subInfo1("회원 유효기간 종료")
                    .build());
        }

    }
}
