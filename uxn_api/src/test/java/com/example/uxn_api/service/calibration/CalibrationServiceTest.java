package com.example.uxn_api.service.calibration;

import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class CalibrationServiceTest {

    private CalibrationRepository calibrationRepository = Mockito.mock(CalibrationRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private CalibrationService calibrationService;
    private Calibration calibration;
//    private CalibrationSaveReqDto saveReqDto;
    private Long userID;
    @BeforeEach
    public void setUpTest() {
        calibrationService = new CalibrationService(calibrationRepository, userRepository);

        userID = 33L;
        calibration = Calibration
                .builder()
                .idx(userID)
                .user(null)
                .contents("99")
                .title("Calibration")
                .createdDate(null)
                .build();
    }

//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("calibration save test")
    void saveTest() {
        // given
        Mockito.when(calibrationRepository.save(any(Calibration.class)))
                .then(returnsFirstArg());

        // when
        CalibrationSaveReqDto calibrationSaveResDto = calibrationService.save(
                new CalibrationSaveReqDto("Calibration", "99", null, null));

        // then
        Assertions.assertEquals(calibrationSaveResDto.getTitle(), "Calibration");
        Assertions.assertEquals(calibrationSaveResDto.getContents(), "99");

        verify(calibrationRepository).save(any());
     }

    @Test
    @DisplayName("calibration read test")
    void readTest() {
        // given
        Mockito.when(calibrationRepository.findById(userID))
                .thenReturn(Optional.of(calibration));

        // when
        CalibrationDetailResDto calibrationDetailResDto = calibrationService.read(userID);

        // then
        Assertions.assertEquals(calibrationDetailResDto.getTitle(), calibration.getTitle());
        Assertions.assertEquals(calibrationDetailResDto.getContents(), calibration.getContents());

        verify(calibrationRepository).findById(userID);
    }
}