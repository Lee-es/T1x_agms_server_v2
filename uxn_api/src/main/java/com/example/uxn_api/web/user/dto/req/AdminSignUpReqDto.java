package com.example.uxn_api.web.user.dto.req;

import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.domain.user.Role;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSignUpReqDto {

    @JsonProperty("user_name")
    private String userName;

    private String email;

    private String password;

    private Gender gender;

    private Set<UserAuthority> authorities;


    public User toEntity(){
        return User
                .builder()
                .userName(userName)
                .email(email)
                .password(password)
                .role(Role.ADMIN)
                .gender(gender)
                .enabled(true)
                .authorities(authorities)
                .build();
    }
}
