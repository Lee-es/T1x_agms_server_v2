package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.staff.Hospital;
import com.example.uxn_common.global.domain.staff.Staff;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffResDto {

    private final Long idx;

    @JsonProperty("staff_name")
    private String staffName;

    private final Hospital hospital;

    private final String birth;

    @JsonProperty("phone_number")
    private String phoneNumber;
    private final String email;

    @JsonProperty("approval_code")
    private String approvalCode;

    private boolean recognize;

    public StaffResDto(Staff entity) {
        this.idx = entity.getIdx();
        this.staffName = entity.getStaffName();
        this.hospital = entity.getHospital();
        this.birth = entity.getBirth();
        this.phoneNumber = entity.getPhoneNumber();
        this.email = entity.getEmail();
        this.approvalCode = entity.getApprovalCode();
    }
}
