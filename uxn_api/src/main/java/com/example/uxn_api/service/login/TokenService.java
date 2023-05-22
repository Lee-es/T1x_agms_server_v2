package com.example.uxn_api.service.login;

import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserToken;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

//    private HashMap<String, String> tokenMap = new HashMap<>();
    private final UserTokenRepository userTokenRepository;

    @Transactional
    public String saveToken(String email, String device, String ip, String refreshToken){
        String token = UUID.randomUUID().toString();
        log.info("saveToken :"+email+ " / " + token + "/" + refreshToken);

        List<UserToken> before = userTokenRepository.findAllByEmailAndDevice(email, device);
        if(before!=null && !before.isEmpty()){
            userTokenRepository.deleteAll(before);
            userTokenRepository.flush();
        }
        userTokenRepository.save(UserToken.builder()
                        .device(device)
                        .ip(ip)
                        .email(email)
                        .refreshToken(refreshToken)
                        .expireTime(LocalDateTime.now().plusMinutes(30))
                        .token(token)
                .build());
        return token;
    }

    @Transactional
    public void updateExpireTime(String email,String token, String refreshToken){
        if(StringUtils.hasText(email) && StringUtils.hasText(token)){
            List<UserToken> before = userTokenRepository.findAllByEmailAndToken(email,token);
            if(before!=null && !before.isEmpty()){
                for(UserToken userToken : before){
                    userToken.setExpireTime(LocalDateTime.now().plusMinutes(30));
                }
            } else {
                log.debug("updateExpireTime fail empty before token");
            }
        } else {
            log.debug("updateExpireTime fail by email or token is null");
            log.debug("token : "+token);
            log.debug("email : "+email);
        }


    }

    public boolean isSavedToken(String email,String refreshToken){
        List<UserToken> before = userTokenRepository.findAllByEmail(email);
        if(before!=null && !before.isEmpty()){
            for(UserToken token : before){
                if(token.getRefreshToken()!=null && token.getRefreshToken().equals(refreshToken)){
                    if(token.getExpireTime().isAfter(LocalDateTime.now())){
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return true;
        }
        return false;

    }

//    @Transactional
//    public void deleteOtherToken(String email,  String device){
//        List<UserToken> before = userTokenRepository.findAllByEmailAndDevice(email,device);
//
//        if(before!=null && !before.isEmpty()){
//            userTokenRepository.deleteAll(before);
//            userTokenRepository.flush();
//        }
//    }
    @Transactional
    public void deleteAllToken(String email){
        if(email==null){
            return;
        }
        List<UserToken> before = userTokenRepository.findAllByEmail(email);

        if(before!=null && !before.isEmpty()){
            userTokenRepository.deleteAll(before);
        }
    }


}
