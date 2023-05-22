package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class UserInfoList {

    @JsonProperty("user_idx")
    private final Long userIdx;

    @JsonProperty("user_name")
    private final String userName; // 이름이 중복일시 이름만 출력하면 하나만 출력됨. @EqualsAndHashCode 안먹이면 모든 데이터를 다 출력.

    private final List<Double> diabetesList;


    public UserInfoList(User user){
        this.userIdx = user.getIdx();
        this.userName = user.getUsername();
        this.diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
    }
}
