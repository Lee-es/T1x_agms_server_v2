package com.example.uxn_api.config;
import com.example.uxn_api.config.jwt.oauth.CustomLogoutHandler;
import com.example.uxn_api.config.jwt.oauth.JWTCheckFilter;
import com.example.uxn_api.config.jwt.oauth.JWTLoginFilter;
import com.example.uxn_api.service.login.CustomAuthenticationFailureHandler;
import com.example.uxn_api.service.login.LoginAttemptService;
import com.example.uxn_api.service.login.TokenService;
import com.example.uxn_api.service.login.UserLoginService;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.Role;
import com.example.uxn_common.global.domain.user.repository.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserLoginService userLoginService;

    private final TokenService tokenService;

    private final UserTokenRepository userTokenRepository;

    private final LoginAttemptService loginAttemptService;

    @Bean
    PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//        return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(userLoginService);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTLoginFilter loginFilter = new JWTLoginFilter(authenticationManager(), userLoginService, tokenService); // 로그인을 처리하는 로그인 필터
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(), userLoginService, tokenService); // 로그인된 토큰을 매번 리퀘스트마다 체크해줄 체크 필터

        loginFilter.setAuthenticationFailureHandler(authenticationFailureHandler());


        http
                .cors().
                configurationSource(corsConfigurationSource()).
                and()
                .csrf().disable()
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함 -> jwt 토큰을 사용하기 때문. -> 세션을 사용하지 않기 때문에 Authentication / Authorization 문제가 생김
                .authorizeRequests()
//                .antMatchers("/api/v1/**").permitAll()
                .antMatchers("/invited/**").anonymous()
                .antMatchers("/api/v1/mail/check-mail/**").anonymous()
                .antMatchers("/api/v1/mail/confirm/**").anonymous()
                .antMatchers("/api/v1/users/login").permitAll()

                .antMatchers("/swagger-ui.html/**").permitAll()
                .antMatchers("/api/v1/sign-up/**").permitAll()
                .antMatchers("/api/v1/user/login/**").permitAll()
                .antMatchers("/api/v1/user/staff-info/check-mail/**").permitAll()
                .antMatchers("/api/v1/user/staff-info/send-mail/**").permitAll()
                .antMatchers("/api/v1/users/check").hasAnyRole(Role.USER.name(), StaffRole.STAFF.name())
                .antMatchers("/api/v1/users/confirm").hasAnyRole(Role.USER.name(), StaffRole.STAFF.name())
                .antMatchers("/api/v1/user/info/**").hasAnyRole(Role.USER.name(), StaffRole.STAFF.name())
                .antMatchers("/api/v1/staff/registration/user").hasRole(Role.USER.name())
                .antMatchers("/api/v1/staff/**").hasRole(StaffRole.STAFF.name())
                .antMatchers("/api/v1/user/staff-info/**").hasAnyRole(Role.USER.name(), StaffRole.STAFF.name())
//                .antMatchers("/api/v1/user/staff-info/**").hasRole(Role.USER.name())
                .antMatchers("/api/v1/note/**").hasRole(Role.USER.name())
                .antMatchers("/api/v1/calibration/**").hasRole(Role.USER.name())
                .and()
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class) // 세션을 사용하지 않고 토큰을 사용하여 인증. -> 로그인 처리
                .addFilterAt(checkFilter, BasicAuthenticationFilter.class) // -> 토큰 검증
                .logout()
                    .logoutUrl("/logout") // 로그아웃 처리 URL
                    .logoutSuccessUrl("/api/v1/users/login")
                    .addLogoutHandler(new CustomLogoutHandler(userTokenRepository))
                    .permitAll()

        ;
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}
