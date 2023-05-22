package com.example.uxn_common.global.domain.user;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordToken {
    @Id
    private String email;
    @Column(length = 500)
    private String token;
    private LocalDateTime createTime;

}
