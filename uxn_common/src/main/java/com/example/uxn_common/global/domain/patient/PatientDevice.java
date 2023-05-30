package com.example.uxn_common.global.domain.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PatientDevice {
    @Id
    @Column(nullable = false)
    Integer deviceId;

    @Column(nullable = false)
    Integer patientId;

    @Column(nullable = false)
    String deviceMac;

    @Column(nullable = false)
    String deviceType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDataTime;
}
