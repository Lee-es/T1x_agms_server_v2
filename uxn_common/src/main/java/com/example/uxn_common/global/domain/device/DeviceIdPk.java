package com.example.uxn_common.global.domain.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceIdPk implements Serializable {

    private Long user;
    private LocalDateTime createDataTime;
}
