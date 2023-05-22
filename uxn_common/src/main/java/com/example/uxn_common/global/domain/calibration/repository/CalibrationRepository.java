package com.example.uxn_common.global.domain.calibration.repository;

import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalibrationRepository extends JpaRepository<Calibration, Long> {


    Calibration findByUserAndIdx(User user, Long idx);
    Calibration findByIdx(Long idx);
    List<Calibration> findByUser(User user);
    List<Calibration> findByUserAndCreatedDateBetween(User user, LocalDateTime start, LocalDateTime end);

    void deleteAllByUser(User User);
}
