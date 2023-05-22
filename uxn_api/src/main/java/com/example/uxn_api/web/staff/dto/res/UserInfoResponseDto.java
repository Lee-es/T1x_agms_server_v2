package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode
public class UserInfoResponseDto {

    @JsonProperty("user_name")
    private final String userName;

    @JsonProperty("diabetes_level")
    private List<Double> diabetesLevel;



    public UserInfoResponseDto(User user){
        this.userName = user.getUsername();
//        this.diabetesLevel = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
        this.diabetesLevel = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());

    }
}
