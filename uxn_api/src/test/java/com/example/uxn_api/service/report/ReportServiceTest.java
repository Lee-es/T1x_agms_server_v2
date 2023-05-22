package com.example.uxn_api.service.report;

import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReportServiceTest {

    private final UserService userService = Mockito.mock(UserService.class);;
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);;
    private final DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);;

    private final NoteRepository noteRepository = Mockito.mock(NoteRepository.class);;

    private final CalibrationRepository calibrationRepository = Mockito.mock(CalibrationRepository.class);;

    private ReportService reportService;
    private User user;
    @BeforeEach
    public void setUpTest() {
        reportService = new ReportService(userService, userRepository
                ,deviceRepository, noteRepository, calibrationRepository);

        user = User.builder()
                .idx(33L)
                .userName("user")
                .email("user123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .role(null)
                .build();
    }

    @Test
    @DisplayName("getReportItemList test")
    void getReportItemListTest() {
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now();

        LocalDateTime startTime = LocalDateTime.of(startDate, LocalTime.of(0, 0, 0));
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.of(23,59,59));

        LocalDate now = startDate;

        LocalDateTime createTime = LocalDateTime.now();
        Device device = Device.builder()
                .createDataTime(createTime)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);

        // given
        Mockito.when(userRepository.findByIdx(user.getIdx())).thenReturn(user);
        Mockito.when(deviceRepository.findByCreateDataTimeBetweenAndUser(
                LocalDateTime.of(now,
                        LocalTime.of(0, 0, 0)
                ),
                LocalDateTime.of(now,
                        LocalTime.of(23, 59, 59)
                ), user)).thenReturn(deviceList);

        try {
            reportService.getReport(user.getIdx(), startDate, endDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("csvFile test")
    void csvFileTest() {
        String startDay = "2022-12-27 12:00";
        String endDay = "2022-12-30 11:59";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime createTime = LocalDateTime.now();
        Device device = Device.builder()
                .createDataTime(createTime)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);

        //Note
        Note note = Note.builder()
                .idx(33L)
                .title("이벤트")
                .contents("점심식사")
                .build();
        List<Note> noteList = Arrays.asList(note);

        //Calibration
        Calibration calibration = Calibration.builder()
                .idx(33L)
                .title("Calibration")
                .contents("99")
                .build();
        List<Calibration> calibrationList = Arrays.asList(calibration);

        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate  end = LocalDate.now();

        LocalDateTime startTime = LocalDateTime.of(start, LocalTime.of(0, 0, 0));
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.of(23,59,59));

        // given
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        Mockito.when(noteRepository.findByUserAndCreateDateBetween(user,startTime,endTime)).thenReturn(noteList);
        Mockito.when(calibrationRepository.findByUserAndCreatedDateBetween(user,startTime,endTime)).thenReturn(calibrationList);
        Mockito.when(deviceRepository.findByCreateDataTimeBetweenAndUser(LocalDateTime.parse(startDay,formatter)
                        , LocalDateTime.parse(endDay,formatter), user)).thenReturn(deviceList);

        // when
//        HttpServletResponse response = mock(HttpServletResponse.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        try {
            reportService.csvFile(start, end, user.getEmail(), response);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}