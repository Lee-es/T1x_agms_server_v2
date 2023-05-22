package com.example.uxn_api.web.login.controller;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.user.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = LoginApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})
class LoginApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    LoginService loginService;

    @Test
    @WithMockUser//SecurityContextImpl authentication - 권한설정 어노테이션
    @DisplayName("login test")
    void loginTest() throws Exception{

        LoginReqDto loginReqDto = LoginReqDto.builder()
                .userId("user")
                .password("uxn1234!@")
                .refreshToken("refreshToken")
                .build();

        Gson gson = new Gson();
        String content = gson.toJson(loginReqDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("login verify test")
    void loginVerifyTest() throws Exception{
        long userID = 33;

        LoginReqDto loginReqDto = LoginReqDto.builder()
//                .userId("user")
                .password("uxn1234!@")
//                .refreshToken("refreshToken")
                .build();


        User user = User.builder()
                .idx(userID)
                .email("abc123@gmail.com")
                .userName("user")
                .password("uxn1234!@")
                .build();


        given(loginService.userVerify(loginReqDto.getUserId(), loginReqDto.getPassword()))
                .willReturn(user);

        Gson gson = new Gson();
        String content = gson.toJson(loginReqDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/login/verify")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.idx").exists())
//                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
//                .andExpect(jsonPath("$.userName").exists())
                .andDo(print());

        verify(this.loginService).userVerify(loginReqDto.getUserId(), loginReqDto.getPassword());
    }
}