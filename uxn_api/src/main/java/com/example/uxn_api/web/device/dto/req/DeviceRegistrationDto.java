package com.example.uxn_api.web.device.dto.req;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRegistrationDto {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("diabetes_level")
    private Double diabetesLevel;

    @JsonProperty("create_data_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDataTime;

    @JsonProperty("user_id")
    private Long user_id;


    @JsonProperty("ae_current")
    private double ae_current;


    @JsonProperty("battery_level")
    private  double batteryLevel;

    @JsonProperty("ref")
    private double ref;

    @JsonProperty("we_p")
    private  double we_p;

    @JsonProperty("ae_p")
    private double ae_p;



    public Device toEntity(){
        return Device
                .builder()
                .deviceId(deviceId)
                .diabetesLevel(diabetesLevel)
                .createDataTime(createDataTime)
                .ae_current(ae_current)
                .batteryLevel(batteryLevel)
                .ref(ref)
                .we_p(we_p)
                .ae_p(ae_p)//-> 최종 포맷
                .user(User.builder().idx(user_id).build())
                .build();
    }
}
