package com.example.uxn_common.global.domain.device;

import com.example.uxn_common.global.entity.BaseTimeEntity;
import com.example.uxn_common.global.domain.user.User;
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
@IdClass(DeviceIdPk.class)
public class Device  {
    @Id
    private LocalDateTime createDataTime;

    @Id
    @ManyToOne
    private User user;

    private String deviceId;

    private Double diabetesLevel;

    private Double ae_current;

    private Double batteryLevel;

    private Double ref;

    private Double we_p;

    private Double ae_p;



}
