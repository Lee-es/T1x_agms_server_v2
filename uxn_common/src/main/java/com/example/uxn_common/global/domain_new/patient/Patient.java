package com.example.uxn_common.global.domain_new.patient;

import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain_new.person.Person;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {
    @Id
    @Column(nullable = false)
    private int personId;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isLocked;

    @Column(columnDefinition = "int2 default 0", nullable = false)
    private short errorCount;

    @Column(columnDefinition = "boolean default 0", nullable = false)
    private boolean eventCheck;

    @Column(columnDefinition = "boolean int 180")
    private int maxGlucose;

    @Column(columnDefinition = "boolean int 70")
    private int minGlucose;

//    @OneToOne(mappedBy = "patient") // 양방향
//    private Person person;
}