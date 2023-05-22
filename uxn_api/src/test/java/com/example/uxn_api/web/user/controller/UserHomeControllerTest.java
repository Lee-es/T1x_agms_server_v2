package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.login.dto.res.LoginCheckResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserHomeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class UserHomeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    // UserHomeController 잡고 있는 Bean 객체에 대해 Mock 형태의 객체를 생성해줌
    @MockBean
    LoginService loginService;;

    @Test
    @WithMockUser
    @DisplayName("checkLogin test")
    void checkLoginTest() throws Exception{
        HttpServletRequest request = null;
        String device = "device1";

        LoginCheckResult result = LoginCheckResult
                .builder()
                .currentWebLoginCount(0)
                .isLoginOtherIp(false)
                .valid(false)
                .deviceLoginCount(0)
                .userToken("token")
                .build();

        given(loginService.checkLogin(request, device, null)).willReturn(result);

        String getUrl = String.format("/api/v1/users/check?device=" + "device1");

//        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
//        info.add("name", "칩");
//        info.add("id", "chip");

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(loginService).checkLogin(request, device, null);
    }

    @Test
    @WithMockUser
    @DisplayName("confirmLogin test")
    void confirmLoginTest() throws Exception{
        HttpServletRequest request = null;
        String device = "device1";

        LoginCheckResult result = LoginCheckResult
                .builder()
                .currentWebLoginCount(0)
                .isLoginOtherIp(false)
                .valid(false)
                .deviceLoginCount(0)
                .userToken("token")
                .build();

        given(loginService.confirmLogin(request, device)).willReturn(result);

        String getUrl = String.format("/api/v1/users/confirm?device=" + "device1");


        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andDo(print());

//        verify(loginService).checkLogin(request, device, null);
    }

    @Test
    @WithMockUser
    @DisplayName("redirectLogin test")
    void redirectLoginTest() throws Exception{
        HttpServletRequest request = null;

//        given(loginService.confirmLogin(request, device)).willReturn(result);

        String getUrl = String.format("/api/v1/users/login");


        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

//        verify(loginService).checkLogin(request, device, null);
    }
}