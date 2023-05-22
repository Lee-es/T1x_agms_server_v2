package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.staff.Hospital;
import com.example.uxn_common.global.domain.staff.Staff;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffListResDto {

    private final Long idx;
    @JsonProperty("staff_name")
    private String staffName;

    private final Hospital hospital;

    private final String email;

    public StaffListResDto(Staff entity){
        this.idx = entity.getIdx();
        this.staffName = entity.getStaffName();
        this.hospital = entity.getHospital();
        this.email = entity.getEmail();
    }
}
