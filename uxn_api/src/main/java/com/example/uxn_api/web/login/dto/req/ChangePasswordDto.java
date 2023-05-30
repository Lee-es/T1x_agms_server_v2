package com.example.uxn_api.web.login.dto.req;

import com.example.uxn_common.global.utils.annotation.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDto {


    @JsonProperty("user_id")
    private String userId;
    @Password
    private String password;
    private String token;
}
