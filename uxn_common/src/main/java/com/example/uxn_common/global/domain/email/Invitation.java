package com.example.uxn_common.global.domain.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Invitation {
    @Column(nullable = false)
    private Integer staff_id;

    @Column(nullable = false)
    private Integer patient_id;

    private String code;
}
