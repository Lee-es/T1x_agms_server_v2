package com.example.uxn_common.global.domain.user;

import com.example.uxn_common.global.domain.staff.Staff;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStaffMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;
    private long staffId;
    private boolean recognize;
    private LocalDateTime createDate;
}
