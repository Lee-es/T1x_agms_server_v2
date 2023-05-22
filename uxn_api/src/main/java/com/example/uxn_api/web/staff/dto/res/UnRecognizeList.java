package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnRecognizeList {

    private Long idx;

    private String name;

    private String birth;

    private String email;

    public UnRecognizeList(User entity) {
        this.idx = entity.getIdx();
        this.name = entity.getUsername();
        this.birth = entity.getBirth();
        this.email = entity.getEmail();
    }


}
