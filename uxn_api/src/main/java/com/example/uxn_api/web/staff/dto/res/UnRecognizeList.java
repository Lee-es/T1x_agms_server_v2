package com.example.uxn_api.web.staff.dto.res;

import com.example.uxn_common.global.domain.user.User;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnRecognizeList {

    private Integer idx;

    private String name;

    private LocalDate birth;

    private String email;

    public UnRecognizeList(User entity) {
        this.idx = entity.getId();
        this.name = entity.getUsername();
        this.birth = entity.getBirth();
        this.email = entity.getEmail();
    }


}
