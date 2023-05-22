package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class UserInfoResDto {

    private final String name;

    private final String email;
    private final List<CheckDeviceResponse> checkDevices;


    public UserInfoResDto(User entity){
        this.name = entity.getUsername();
        this.email = entity.getEmail();
        this.checkDevices = entity.getDevices().stream().map(CheckDeviceResponse::new).collect(Collectors.toList());
    }


}
