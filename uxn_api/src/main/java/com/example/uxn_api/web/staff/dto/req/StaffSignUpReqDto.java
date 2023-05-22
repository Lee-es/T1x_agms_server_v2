package com.example.uxn_api.web.staff.dto.req;

import com.example.uxn_common.global.domain.staff.Hospital;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.Gender;
import com.example.uxn_common.global.domain.user.UserAuthority;
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
public class StaffSignUpReqDto {

    @JsonProperty("staff_id")
    private String staffId;
    @JsonProperty("staff_name")
    private String staffName;

    private String email;

    @Password
    private String password;

//    @JsonProperty("check_pwd")
//    private String checkPwd;

    private Hospital hospital;

    private String birth;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private Gender gender;

    private Set<StaffAuthority> authorities;

    @JsonProperty("approval_code")
    private String approvalCode;

    public Staff toEntity(){
        return Staff
                .builder()
                .staffName(staffName)
                .email(email)
                .password(password)
                .hospital(hospital)
                .role(StaffRole.STAFF)
                .enabled(true)
                .authorities(authorities)
                .birth(birth)
                .phoneNumber(phoneNumber)
                .gender(gender)
                .approvalCode(approvalCode)
                .build();
    }


}
