package com.example.uxn_api.service.login;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.login.dto.res.LoginCheckResult;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.ChangePasswordToken;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserToken;
import com.example.uxn_common.global.domain.user.repository.ChangePasswordTokenRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserTokenRepository userTokenRepository;
    private final TokenService tokenService;

    private final MailSendService mailSendService;

    private final ChangePasswordTokenRepository changePasswordTokenRepository;

    private static final Long MAX_EXPIRE_TIME = 30L;
//    private static final Long MAX_EXPIRE_TIME = 1L;

    private final LogService logService;

    public void plusErrorCount(String email) {
        userRepository.updateErrorCount(email);
    }

    public void resetErrorCount(String email) {
        User user = userRepository.findByEmail(email);
        user.errorCountUpdate(0);
    }

    @Transactional(readOnly = true)
    public boolean emailAuthenticated(String email, String pwd) {
        Staff staff = staffRepository.findByEmailAndPassword(email, pwd);
        User user = userRepository.findByEmailAndPassword(email, pwd);

        if(user != null){
            return user.isEmailVerifiedSuccess();
        }else{
            return staff.isEmailVerifiedSuccess();
        }
    }

    public Login userVerify(String userId, String pwd) {
        log.info("userVerify 48.");
        User user = userRepository.findByEmail(userId);
        Staff staff = staffRepository.findByEmail(userId);

        if(passwordEncoder.matches(pwd, user.getPassword())) {
            log.info("유저");
            return user;
        }else if(passwordEncoder.matches(pwd, staff.getPassword())) {
            log.info("스탶");
            return staff;
        }else {
            log.info("로그인 실패.");
            return null;
        }
    }



    @Transactional
    public LoginCheckResult confirmLogin(HttpServletRequest request, String device){
        List<UserToken> removeList = new ArrayList<>();
        LoginCheckResult result = new LoginCheckResult();
        String email = null;
        String ip = getClientIP(request);
        List<UserToken> userTokenList = null;
        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            email = authenticationToken.getName();
            log.debug("confirmLogin email:"+email);
        }catch (Exception e){
            e.printStackTrace();
        }
        String savedToken = null;
        if(email !=null){
            String bearer = request.getHeader(HttpHeaders.AUTHORIZATION); //
            String token = null;
            if(bearer!=null){
                token = bearer.substring("Bearer ".length());
            }

            savedToken = tokenService.saveToken(email,device,ip, token);

        } else {
            log.error("confirmLogin but email null");
        }


        return checkLogin(request,"web",savedToken );
    }

    public LoginCheckResult checkLogin(HttpServletRequest request, String device, String savedToken){
        String email = null;
        String token = request.getHeader("uuid");
        String ip = getClientIP(request);
        List<UserToken> userTokenList = null;
        try{
            UsernamePasswordAuthenticationToken authenticationToken =
                    (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            email = authenticationToken.getName();
            log.debug("confirmLogin email:"+email);
        }catch (Exception e){
            e.printStackTrace();
        }

        log.debug("checkLogin email:"+ email);
        LoginCheckResult result = new LoginCheckResult();
        result.setUserToken(savedToken);
        userTokenList = userTokenRepository.findAllByEmail(email);
        log.debug("checkLogin email:"+email);
        boolean found = false;
        if(StringUtils.hasText(savedToken)){
           found = true;
        }
        if(userTokenList !=null){
            for(UserToken userToken : userTokenList){

                if(userToken.getToken()!=null && token!=null && userToken.getToken().equals(token)){
                    if(!userToken.getExpireTime().isAfter(LocalDateTime.now())){
                        found = true;
                    }
                } else {
                    if(StringUtils.hasText(userToken.getDevice())  && userToken.getDevice().equalsIgnoreCase("web")){
                        if(userToken.getExpireTime().isAfter(LocalDateTime.now())){
                            result.addWebLoginCounter();
                            if(ip!=null && userToken.getIp()!=null && !ip.equals(userToken.getIp()) ){
                                result.setLoginOtherIp(true);
                                if(token==null){
                                    try {
                                        sendPasswordResetLink(email);
                                    } catch (MessagingException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }

                    }  else if(StringUtils.hasText(userToken.getDevice())  && userToken.getDevice().equalsIgnoreCase("device")){
                        if(userToken.getExpireTime()!=null && userToken.getExpireTime().isAfter(LocalDateTime.now())){
                            result.addDeviceLoginCounter();
                            if(ip!=null && userToken.getIp()!=null && !ip.equals(userToken.getIp()) ){
                                result.setLoginOtherIp(true);
                            }
                        }

                    } else {

                    }
                }

            }
        }
        result.setValid(found);
        return result;
    }
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private void sendPasswordResetLink(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        String key = MailSendService.createKey();
        changePasswordTokenRepository.save(ChangePasswordToken.builder()
                .createTime(LocalDateTime.now())
                .email(email)
                .token(key)
                .build());
        if(user != null){
            Long idx = user.getIdx();
            mailSendService.verificationMailSend(email,5, idx,key);
            logService.log(true, ActivityKind.FIND_PASSWORD,"비밀번호 재설정 링크 전송", email,null,null,null);
        } else {
            Staff staff = staffRepository.findByEmail(email);
            if(staff!=null){
                Long idx = staff.getIdx();
                mailSendService.verificationMailSend(email,5, idx,key, false);
                logService.log(false, ActivityKind.FIND_PASSWORD,"비밀번호 재설정 링크 전송", email,null,null,null);
            } else {
                logService.log(true, ActivityKind.FIND_PASSWORD,"user not found fail", email,null,null,null);
                throw new LoginException("not found user", ErrorCode.NOT_FOUND);
            }

        }
    }
}
