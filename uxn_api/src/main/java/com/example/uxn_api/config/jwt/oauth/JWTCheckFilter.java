package com.example.uxn_api.config.jwt.oauth;

import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.service.login.TokenService;
import com.example.uxn_api.service.login.UserLoginService;
import com.example.uxn_api.web.user.dto.res.TokenVerifyResult;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

@Slf4j
public class JWTCheckFilter extends BasicAuthenticationFilter {

//    private final UserService userService;

    private final UserLoginService userLoginService;
    private final TokenService tokenService;

    public JWTCheckFilter(AuthenticationManager authenticationManager, UserLoginService userLoginService, TokenService tokenService){
        super(authenticationManager);
        this.userLoginService = userLoginService;
        this.tokenService = tokenService;
    }

    // 토큰검사
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("doFilterInternal 36");
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION); //
        if(bearer == null || !bearer.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }
        String token = bearer.substring("Bearer ".length());
//        if(!tokenService.isTokenValidate(token)){
//            log.debug("login check not valid token:"+token);
//            return;
//        }

        TokenVerifyResult result = JWTUtil.validVerify(token); // 요청에 쓰이는 토큰 -> 유효성 검사 후 유효하지 않으면 Token is not valid -> 리프레시 토큰 으로 대체 -> 리프레시 만료되면 재 로그인
        if(result.isSuccess()) {
            log.debug("result.isSuccess() 45 : " + result.getAuthority());


            if(result.getAuthority().equals("ROLE_USER")) {
                //user 전체를 가져오면 device 테이블로 로드되어 부하가 걸려서 user 이메일과 authority만 가져오는 걸로 변경 - 23.05.19 ykw
                //수정 전
//                User user = (User) userLoginService.loadUserByUsername(result.getUserId()); // 유저객체 말고 공통적으로 받을수 있는 객체 생성할것.

//                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
//                        user.getEmail(), null, user.getAuthorities()
//                );

                //수정 후
                String email = userLoginService.findEmailByUserEmail(result.getUserId());

                Long userID = userLoginService.findUserIDByUserEmail(result.getUserId());
                UserAuthority authority = userLoginService.findUserAuthorityByUserID(userID);
                HashSet<UserAuthority> authorities = new HashSet<>();
                authorities.add(authority);

                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                        email, null, authorities
                );

//                String email = user.getEmail();
//                if(!tokenService.isSavedToken(email,token)){
//                    chain.doFilter(request, response);
//                    return;
//                }

                log.debug("result.isSuccess() email : " + email);

                SecurityContextHolder.getContext().setAuthentication(userToken);
                chain.doFilter(request, response);
            }else {
                Staff staff = (Staff) userLoginService.loadUserByUsername(result.getUserId()); // 유저객체 말고 공통적으로 받을수 있는 객체 생성할것.
                UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                        staff.getEmail(), null, staff.getAuthorities()
                );
                String email = staff.getEmail();
//                if(!tokenService.isSavedToken(email,token)){
//                    chain.doFilter(request, response);
//                    return;
//                }
                log.debug("result.isSuccess() email : " + email);

                SecurityContextHolder.getContext().setAuthentication(userToken);
                chain.doFilter(request, response);
            }
        }
        else {
            log.info("Token is not valid");
        }
//        else{
//            throw new TokenExpiredException("Token is not valid");
//
//        }
    }
}
