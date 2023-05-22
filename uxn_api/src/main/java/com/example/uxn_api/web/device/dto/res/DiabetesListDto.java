package com.example.uxn_api.web.device.dto.res;

import com.example.uxn_common.global.domain.device.Device;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DiabetesListDto {

    private LocalDate crateDataDay;

    private List<DiabetesDto> diabetesDtoList;

    public DiabetesListDto(LocalDate crateDataDay, List<DiabetesDto> diabetesDtoList) {
        this.crateDataDay = crateDataDay;
        this.diabetesDtoList = diabetesDtoList;
    }
}
