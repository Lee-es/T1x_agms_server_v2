package com.example.uxn_api.web.staff.dto.req;

import com.example.uxn_common.global.domain.staff.Staff;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDto {

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("staff_idx")
    private Long staffIdx;

    @JsonProperty("user_idx")
    private Long userIdx;

    private String code;
    public Staff toEntity(){
        return Staff
                .builder()
                .staffName(staffName)
                .build();
    }

}
