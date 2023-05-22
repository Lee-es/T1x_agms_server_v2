package com.example.uxn_api.web.user.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserInfoFromDevices {


    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("diabetes_level")
    private Long diabetesLevel;

    @JsonProperty("created_data_time")
    private List<LocalDateTime> createdDataTime;


    public UserInfoFromDevices(User entity){
        this.userName = entity.getUsername();
        this.createdDataTime = entity.getDevices().stream().map(Device::getCreateDataTime).collect(Collectors.toList());
    }


}
