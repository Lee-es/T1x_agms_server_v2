package com.example.uxn_api.web.user.dto.req;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.domain.user.Role;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.example.uxn_common.global.utils.annotation.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpReqDto {

    @JsonProperty("user_name")
    private String userName;

    private String email;

    @Password
    private String password;

    private Gender gender;

    private String birth;

    private Set<UserAuthority> authorities;

//    private List<Device> deviceList;

    public User toEntity(){
        return User
                .builder()
                .userName(userName)
                .email(email)
                .password(password)
                .gender(gender)
                .role(Role.USER)
                .birth(birth)
                .enabled(true)
                .authorities(authorities)
                .build();
    }

}
