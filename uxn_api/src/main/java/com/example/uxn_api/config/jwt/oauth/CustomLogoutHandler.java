package com.example.uxn_api.config.jwt.oauth;

import com.example.uxn_api.config.CommonConstant;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserToken;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final UserTokenRepository userTokenRepository;

    public CustomLogoutHandler(UserTokenRepository userTokenRepository) {
        this.userTokenRepository = userTokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {

        try{

            String token = request.getHeader("uuid");
            log.debug("token : "+token);
            if(StringUtils.hasText(token)){
                List<UserToken> before = userTokenRepository.findAllByToken(token);
                if(before!=null && !before.isEmpty()){
                    userTokenRepository.deleteAll(before);
                    userTokenRepository.flush();
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
//
//        List<UserToken> tokens = userTokenRepository.findAllByToken(request.getHeader("refresh_token"));
//        if(tokens!=null){
//            userTokenRepository.deleteAll(tokens);
//        }


    }
}