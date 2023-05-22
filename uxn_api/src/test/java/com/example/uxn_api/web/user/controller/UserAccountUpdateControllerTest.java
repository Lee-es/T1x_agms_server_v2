package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.login.dto.req.ChangePasswordDto;
import com.example.uxn_api.web.user.dto.req.UserSignUpReqDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAccountUpdateController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})

class UserAccountUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;
    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    LogService logService;

    @Test
    @WithMockUser
    @DisplayName("updatePassword test")
    void updatePasswordTest() throws Exception {

        ChangePasswordDto req = ChangePasswordDto.builder()
                .userId("userID")
                .password("uxn1234!@")
                .token("token")
                .build();

        String email = "user123@gmail.com";

//        given(userService.updatePassword(email, passwordEncoder.encode(req.getPassword()), req.getToken() ))
//                .willReturn(null);//return void
//        doNothing().when(userService).updatePassword(email, passwordEncoder.encode(req.getPassword()), req.getToken());

        Gson gson = new Gson();
        String content = gson.toJson(req);
//        String content2 = gson.toJson(email);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/user/account/password-update/" + email)
//                        .content(content2)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").exists())//id값만 리턴 한다.
                .andDo(print());

//        verify(this.userService).saveUser(user);
        verify(this.userService).updatePassword(email, passwordEncoder.encode(req.getPassword()), req.getToken());
    }
}