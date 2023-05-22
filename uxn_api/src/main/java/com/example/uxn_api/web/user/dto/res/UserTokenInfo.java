package com.example.uxn_api.web.user.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenInfo {

    @JsonProperty("auth_token")
    private String authToken;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
