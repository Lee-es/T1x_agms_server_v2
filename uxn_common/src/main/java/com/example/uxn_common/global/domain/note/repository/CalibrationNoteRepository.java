package com.example.uxn_common.global.domain.note.repository;

import com.example.uxn_common.global.domain.note.CalibrationNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CalibrationNoteRepository extends JpaRepository<CalibrationNote, Integer> {


//    CalibrationNote findByDeviceID(int deviceId);

    List<CalibrationNote> findByDeviceID(Integer deviceId);
    List<CalibrationNote> findByDeviceIDAndCreatedDateBetween(Integer deviceId, LocalDateTime start, LocalDateTime end);

    void deleteAllByDeviceID(Integer deviceId);
}
