package com.example.uxn_api.web.staff.dto.req;

import com.example.uxn_common.global.domain.staff.Hospital;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.utils.annotation.Password;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffUpdateReqDto {


    @JsonProperty("staff_name")
    private String staffName;

    private String email;


    private Hospital hospital;

    private String birth;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private Gender gender;


    @JsonProperty("approval_code")
    private String approvalCode;

    public Staff toEntity(){
        return Staff
                .builder()
                .staffName(staffName)
                .email(email)

                .hospital(hospital)
                .role(StaffRole.STAFF)
                .enabled(true)

                .birth(birth)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .approvalCode(approvalCode)
                .build();
    }


}
