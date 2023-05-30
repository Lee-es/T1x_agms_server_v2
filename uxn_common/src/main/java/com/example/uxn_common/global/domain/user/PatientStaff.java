package com.example.uxn_common.global.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientStaff {
    @Id
    @Column(nullable = false)
    private Integer staffId;

    @Id
    @Column(nullable = false)
    private Integer patientId;

    @Column(nullable = false)
    private Boolean isRecognized;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

}
