package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.DeviceValue;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

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
        //user의 등록된 device 찾기

        //해당 device의 모든 데이터 얻기
        this.diabetesLevel = null;//user.getDevices().stream().map(DeviceValue::getValue).collect(Collectors.toList());

    }
}
