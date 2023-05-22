package com.example.uxn_common.global.domain.user;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String email;
    @Column(length = 500)
    private String token;
    private String ip;
    private String device;
    private LocalDateTime expireTime;
    @Column(length = 1000)
    private String refreshToken;
}
