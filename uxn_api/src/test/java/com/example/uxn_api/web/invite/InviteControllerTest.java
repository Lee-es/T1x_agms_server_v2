package com.example.uxn_api.web.invite;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.email.MailApiController;
import com.example.uxn_common.global.domain.email.Invite;
import com.example.uxn_common.global.domain.email.repository.InviteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InviteController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})
class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    InviteRepository inviteRepository;
    @MockBean
    UserService userService;
    @MockBean
    StaffService staffService;

    private User user;
    private Staff staff;

    @BeforeEach
    public void setUpTest() {
        user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user1234@gmail.com")
                .build();

        staff = Staff.builder()
            .idx(111L)
            .staffName("staff")
            .email("staff1234@gmail.com")
            .build();
    }

    @Test
    @WithMockUser
    @DisplayName("infoFromUser test")
    void infoFromUserTest() throws Exception{
        //테스트용 가짜 데이터 주입
        String code = MailSendService.createKey();

        Invite invite = Invite.builder()
                .idx(33L)
                .code(code)
                .userId(33L)
                .staff(staff)
                .build();

        given(inviteRepository.findByCode(code)).willReturn(invite);
        given(userService.findUser(invite.getIdx())).willReturn(user);
        doNothing().when(staffService).registration(staff.getEmail(), user.getIdx(),true);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.get("/invited/" + code)
//                        .param("code", code)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        //code null test
        code = null;
        mockMvc.perform(MockMvcRequestBuilders.get("/invited/" + code)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isFound())
                .andDo(print());

        verify(staffService).registration(staff.getEmail(), user.getIdx(),true);
    }
}