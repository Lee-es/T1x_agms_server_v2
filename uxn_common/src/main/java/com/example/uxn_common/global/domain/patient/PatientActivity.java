package com.example.uxn_common.global.domain.patient;

import com.example.uxn_common.global.domain.user.ActivityKind;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private ActivityKind activityKind = ActivityKind.NONE;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String subInfo1;
    private String subInfo2;
    private String subInfo3;

    private String summary;

    @Column(columnDefinition = "TEXT")
    private String text;
}
