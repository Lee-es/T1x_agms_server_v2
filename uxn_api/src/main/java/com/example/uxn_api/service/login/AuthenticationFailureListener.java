package com.example.uxn_api.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    @Autowired
    private HttpServletRequest request;



    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
//        final String xfHeader = request.getHeader("X-Forwarded-For");
//        if (xfHeader == null) {
//            loginAttemptService.loginFailed(request.getRemoteAddr(),null);
//        } else {
//            loginAttemptService.loginFailed(xfHeader.split(",")[0],null);
//        }
    }
}
