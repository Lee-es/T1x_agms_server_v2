package com.example.uxn_common.global.domain.device.repository;

import com.example.uxn_common.global.domain.device.DeviceValue;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceValueRepository extends JpaRepository<DeviceValue, Integer> {
    List<DeviceValue> findByDeviceID(Integer deviceID);

    List<DeviceValue> findAllyDeviceID(Integer deviceID);

    List<DeviceValue> findByCreatedAtBetweenAndDeviceID (LocalDateTime startDay, LocalDateTime endDay, Integer deviceID);

    void deleteAllByDeviceId(Integer deviceId);

    @Query("SELECT d.value FROM DeviceValue d WHERE d.id = :deviceID")
    Double getDeviceValueByDeviceID(Integer deviceID);
}
