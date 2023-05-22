package com.example.uxn_api.web.login.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginCheckResult {
    private int currentWebLoginCount = 0;
    private boolean isLoginOtherIp = false;
    private boolean valid = false;
    private int deviceLoginCount = 0;
    public void addWebLoginCounter(){
        this.currentWebLoginCount++;
    }
    public void addDeviceLoginCounter(){
        this.deviceLoginCount++;
    }

    private String userToken;
}
