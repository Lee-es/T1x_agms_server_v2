package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LowBloodSugarDto { // 70미만인 당뇨수치 + 그때의 시간

    private LocalDateTime lowBloodSugarTime;

    private Double lowDiabetes;

    public LowBloodSugarDto(Device entity) {
        this.lowBloodSugarTime = entity.getCreateDataTime();
        this.lowDiabetes = entity.getDiabetesLevel();
    }

}
