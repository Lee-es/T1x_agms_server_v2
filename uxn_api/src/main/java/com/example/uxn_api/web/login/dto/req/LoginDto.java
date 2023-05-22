package com.example.uxn_api.web.login.dto.req;

import com.example.uxn_common.global.utils.annotation.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {


    @JsonProperty("user_id")
    private String userId;
    @Password
    private String password;
}
