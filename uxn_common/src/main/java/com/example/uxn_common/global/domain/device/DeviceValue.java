package com.example.uxn_common.global.domain.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
public class DeviceValue {
    @Id
    @Column(nullable = false)
    Integer deviceId;

    @Id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @Column(columnDefinition = "LocalDateTime default now", nullable = false)
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Id
    @Column(nullable = false)
    private Short type1;

    @Column(nullable = false)
    private Double value;
}
