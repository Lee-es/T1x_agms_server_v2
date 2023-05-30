package com.example.uxn_common.global.domain.note.repository;

import com.example.uxn_common.global.domain.note.EventNote;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

public interface EventNoteRepository extends JpaRepository<EventNote, Integer> {
    List<EventNote> findByDeviceID(Integer deviceId);
    List<EventNote> findByDeviceIDAndCreatedDateBetween(Integer deviceId, LocalDateTime start, LocalDateTime end);

    void deleteAllByDeviceID(Integer deviceId);

}
