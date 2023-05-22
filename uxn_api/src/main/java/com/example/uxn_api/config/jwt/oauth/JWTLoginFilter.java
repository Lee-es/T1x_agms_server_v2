package com.example.uxn_api.config.jwt.oauth;

import com.example.uxn_api.service.login.LoginAttemptService;
import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.service.login.TokenService;
import com.example.uxn_api.service.login.UserLoginService;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_api.web.user.dto.res.TokenVerifyResult;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final UserService userService;
    private final UserLoginService userLoginService;

    private final TokenService tokenService;



    public JWTLoginFilter(AuthenticationManager authenticationManager, UserLoginService userLoginService, TokenService tokenService){
        super(authenticationManager);
        this.userLoginService = userLoginService;
        this.tokenService = tokenService;
        setFilterProcessesUrl("/api/v1/login/**");
    }


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, LoginException { // 사용자 인증 처리
        log.debug("attemptAuthentication 52");
        LoginReqDto login = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

        userLoginService.loginAttempt(request.toString(), login.getUserId(), getClientIP(request));

        if(login.getRefreshToken() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( // 토큰  인증되기 전이기 때문에  Authorities null
                    login.getUserId(), login.getPassword(),null
            );

            // user details...
            return getAuthenticationManager().authenticate(token); // getAuthenticationManager 토큰 검증 요청

        }else{ // 리프레쉬 토큰으로 들어오면 토큰이 유효한지 검증 한다. -> 기존 토큰이 만료됐을시 갖고 있던 리프레시 토큰으로 갱신요청을 한 후 응답받은 리프레시 토큰을 사용하여 유효성 검증. -> 리프레시 토큰이 만료되면 재 로그인
            log.debug("before check token validate");
//            if(!tokenService.isTokenValidate(login.getRefreshToken())){
//                log.debug("before check token validate");
//                return null;
//            }

            TokenVerifyResult verify = JWTUtil.refreshVerify(login.getRefreshToken()); // 유효하지 않으면 여기서 끝남.
            if(verify.isSuccess()){ // 유효 하면 ->

                if(verify.getAuthority().equals("ROLE_USER")){
                    User user = (User) userLoginService.loadUserByUsername(verify.getUserId());
                    return new UsernamePasswordAuthenticationToken(
                            user,user.getAuthorities()
                    );
                }else {
                    Staff staff = (Staff) userLoginService.loadUserByUsername(verify.getUserId());
                    return new UsernamePasswordAuthenticationToken(
                            staff,staff.getAuthorities()
                    );
                }

            }else{
                return null;
            }
        }

    }

    @Override
    protected void successfulAuthentication( // 검증이 제대로 됐다면 해당 메소드가 호출됨.
                                             HttpServletRequest request,
                                             HttpServletResponse response,
                                             FilterChain chain,
                                             Authentication authResult) throws IOException, ServletException {
        log.debug("successfulAuthentication 102");
        if(authResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).get(0).equals("ROLE_USER")){

            User user = (User) authResult.getPrincipal();
            String refreshToken = JWTUtil.makeRefreshToken(user);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer"); // 인증 테스트 -> KEY : Authorization Value : Bearer + 토큰 값.
            // User
            response.setHeader("auth_token", JWTUtil.makeAuthToken(user));
            response.setHeader("refresh_token", refreshToken);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        response.getOutputStream().write(objectMapper.writeValueAsBytes(user)); // 인증된 토큰을 유저객체에 발행 -> 리턴 값이 유저객체로 넘어옴
            response.getWriter().write("auth_token: " + JWTUtil.makeAuthToken(user) + " refresh_token: " + refreshToken);
            tokenService.updateExpireTime(user.getEmail(), request.getHeader("uuid"), refreshToken);
            userLoginService.doWhenLoginSuccess(getClientIP(request),user.getEmail(), request.toString());
        }else {
            Staff staff = (Staff)authResult.getPrincipal();
            // Staff
            String refreshToken = JWTUtil.makeStaffRefreshToken(staff);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer"); // 인증 테스트 -> KEY : Authorization Value : Bearer + 토큰 값.
            response.setHeader("staff_auth_token", JWTUtil.makeStaffToken(staff));
            response.setHeader("staff_refresh_token", refreshToken);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//        response.getOutputStream().write(objectMapper.writeValueAsBytes(user)); // 인증된 토큰을 유저객체에 발행 -> 리턴 값이 유저객체로 넘어옴
            response.getWriter().write("staff_token: " + JWTUtil.makeStaffToken(staff) + " staff_refresh_token: " + refreshToken);
            tokenService.updateExpireTime(staff.getEmail(),request.getHeader("uuid"), refreshToken);
            userLoginService.doWhenLoginSuccess(getClientIP(request),staff.getEmail(), request.toString());
        }

    }
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
