package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiabetesGroupDto {

    private List<LocalDateTime> createDataTime;

    @JsonProperty("diabetes_list")
    private List<Double> diabetesList;

    public DiabetesGroupDto(User user) {
        this.createDataTime = user.getDevices().stream().map(Device::getCreateDataTime).collect(Collectors.toList());
        this.diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
    }
}
