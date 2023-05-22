package com.example.uxn_common.global.domain.staff;

import com.example.uxn_common.global.domain.user.ActivityKind;
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
public class StaffActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String summary;

    private Long staffIdx;
    private Long userIdx;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ActivityKind activityKind = ActivityKind.NONE;

    private String subInfo1;
    private String subInfo2;
    private String subInfo3;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Builder.Default
    LocalDateTime createTime = LocalDateTime.now();

}
