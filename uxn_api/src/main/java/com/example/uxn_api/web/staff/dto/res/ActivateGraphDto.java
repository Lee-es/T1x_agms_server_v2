package com.example.uxn_api.web.staff.dto.res;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ActivateGraphDto {

    private String time;

    private Long diabetesCount;

    private Long dateCount;

    private Double activatePercent;

}
