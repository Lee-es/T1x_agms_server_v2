package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.staff.Staff;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
//@EqualsAndHashCode
public class UserListDto {

    private List<UserInfoList> userList;

    /*public UserListDto(Staff staff){
        this.userList = staff.getUserList().stream().map(UserInfoList::new).distinct().collect(Collectors.toList()); // 바로 받을경우 중복이름일때 하나만 노출이 된다.

    }*/
}
