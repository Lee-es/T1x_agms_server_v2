package com.example.uxn_api.web.staff.dto.res;

import lombok.*;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TimeActivate { // y 축 : 활성화 % , x 축 : 시간(일) -> 1시간 60개

    private String time;

    private int count;

    private Double activatePercent;

}
