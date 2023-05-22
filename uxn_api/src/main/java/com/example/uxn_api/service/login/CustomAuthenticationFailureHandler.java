package com.example.uxn_api.service.login;

import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final UserLoginService userLoginService;

    public CustomAuthenticationFailureHandler(UserLoginService userLoginService){
        this.userLoginService = userLoginService;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        int statusCode = HttpStatus.UNAUTHORIZED.value();
        try{
            statusCode = Integer.valueOf(exception.getMessage());
        }catch (Exception e){}
        final String xfHeader = request.getHeader("X-Forwarded-For");
        String ip = null;
        if (xfHeader == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = xfHeader.split(",")[0];
        }
        try{
            if(request.getRequestURI().endsWith("/login")){
                log.debug("/loign login failed");
                if(exception instanceof BadCredentialsException){
                    log.debug("BadCredentialsException");
                    userLoginService.doWhenLoginFail(ip,request.toString());

                } else {
                    userLoginService.doWhenLoginFail(ip,statusCode,request.toString());
                }
//                LoginReqDto login = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);
//                if(login!=null){
//                    log.debug("onAuthenticationFailure login dto ; "+login.getUserId());
//                    String userId = login.getUserId();
//                    if (xfHeader == null) {
//                        loginAttemptService.loginFailed(request.getRemoteAddr(),userId);
//                    } else {
//                        loginAttemptService.loginFailed(xfHeader.split(",")[0],userId);
//                    }
//                } else {
//                    log.debug("onAuthenticationFailure login failed, but no login dto");
//                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }




        log.debug("onAuthenticationFailure, "+exception.getMessage());

        response.setStatus(statusCode);
        Map<String, Object> data = new HashMap<>();
        data.put(
                "timestamp",
                Calendar.getInstance().getTime());
        if(statusCode!=HttpStatus.UNAUTHORIZED.value()){
            data.put(
                    "exception","UNAUTHORIZED");
        } else {
            if(IsValidISO(exception.getMessage())){
                data.put(
                        "exception",
                        exception.getMessage());
            } else {
                data.put(
                        "exception","UNAUTHORIZED");
            }
        }




        response.getOutputStream()
                .println(objectMapper.writeValueAsString(data));
    }
    private static boolean IsValidISO(String input)
    {
        if(input!=null && input.equalsIgnoreCase("자격 증명에 실패하였습니다.")){
            return false;
        }
        if (input!=null && input.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
            return false;
        }
        /*CharsetDecoder cd =
                Charset.availableCharsets().get("ISO-8859-1").newDecoder();
        try {
            cd.decode(ByteBuffer.wrap(input.getBytes(StandardCharsets.ISO_8859_1)));
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }
}