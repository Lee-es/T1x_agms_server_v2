package com.example.uxn_common.global.domain_new.patient;

import com.example.uxn_common.global.domain.user.ActivityKind;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PatientActivity {
    @Id
    private Long patientId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ActivityKind activityId = ActivityKind.NONE;

    private String createTime;

    private String subInfo1;
    private String subInfo2;
    private String subInfo3;

    private String summary;

    @Column(columnDefinition = "TEXT")
    private String text;
}
