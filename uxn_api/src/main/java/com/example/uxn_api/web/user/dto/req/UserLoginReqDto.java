package com.example.uxn_api.web.user.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginReqDto {

    @JsonProperty("user_id")
    private String userId;

    private String password;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
