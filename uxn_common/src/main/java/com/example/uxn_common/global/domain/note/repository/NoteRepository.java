package com.example.uxn_common.global.domain.note.repository;

import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

//    @Query("SELECT n FROM Note n ORDER BY n.createdAt ASC ")

    Note findByUserAndIdx(User user, Long idx);
    Note findByIdx(Long idx);
    List<Note> findByUser(User user);

    void deleteAllByUser(User User);
    List<Note> findByUserAndCreateDateBetween(User user, LocalDateTime start, LocalDateTime end);

}
