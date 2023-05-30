package com.example.uxn_api.web.login.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {

    @JsonProperty("user_id")
    private String userId;

    private String password;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
