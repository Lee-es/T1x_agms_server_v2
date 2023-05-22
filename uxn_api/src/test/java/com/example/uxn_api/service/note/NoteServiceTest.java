package com.example.uxn_api.service.note;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class NoteServiceTest {
    private NoteRepository noteRepository = Mockito.mock(NoteRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private NoteService noteService;
    private Note note;
    private Long userID;

    @BeforeEach
    public void setUpTest() {
        noteService = new NoteService(noteRepository, userRepository);

        userID = 33L;
        note = Note
                .builder()
                .idx(userID)
                .user(null)
                .contents("99")
                .title("Event")
                .createDate(null)
                .build();
    }

    @Test
    @DisplayName("note save test")
    void saveTest() {
        // given
        Mockito.when(noteRepository.save(any(Note.class)))
                .then(returnsFirstArg());

        // when
        NoteSaveReqDto noteSaveResDto = noteService.save(
                new NoteSaveReqDto("Event", "99", null, null));

        // then
        Assertions.assertEquals(noteSaveResDto.getTitle(), "Event");
        Assertions.assertEquals(noteSaveResDto.getContents(), "99");

        verify(noteRepository).save(any());
    }

    @Test
    @DisplayName("note read test")
    void readTest() {
        // given
        Mockito.when(noteRepository.findById(userID))
                .thenReturn(Optional.of(note));

        // when
        NoteDetailResDto noteDetailResDto = noteService.read(userID);

        // then
        Assertions.assertEquals(noteDetailResDto.getTitle(), note.getTitle());
        Assertions.assertEquals(noteDetailResDto.getContents(), note.getContents());

        verify(noteRepository).findById(userID);
    }
}