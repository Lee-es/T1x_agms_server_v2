package com.example.uxn_api.web.user.dto.res;

import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UserInfoToDiabetes {

    @JsonProperty("user_name")
    private String userName;

    private final String email;

    public UserInfoToDiabetes(User entity){
        this.userName = entity.getUsername();
        this.email = entity.getEmail();
    }
}
