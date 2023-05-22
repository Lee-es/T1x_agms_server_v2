package com.example.uxn_common.global.domain.user;

import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.note.Note;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserActivity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String summary;
    private Long userIdx;
    private Long staffIdx;

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
