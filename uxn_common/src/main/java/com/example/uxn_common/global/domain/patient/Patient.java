package com.example.uxn_common.global.domain.patient;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @Column(nullable = false)
    private Integer userId;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private Boolean isLocked;

    @Column(columnDefinition = "int2 default 0", nullable = false)
    private Short errorCount;

    @Column(columnDefinition = "boolean default 0", nullable = false)
    private Boolean eventCheck;

    @Column(columnDefinition = "boolean int 180")
    private Integer maxGlucose;

    @Column(columnDefinition = "boolean int 70")
    private Integer minGlucose;

//    @OneToOne(mappedBy = "patient") // 양방향
//    private Person person;
}