package com.example.uxn_common.global.domain.staff;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class Staff {
    @Id
    @Column(nullable = false)
    private Integer userId;

    private Integer hospitalId;

    private Integer patientCount;

    private String approvalCode;
}
