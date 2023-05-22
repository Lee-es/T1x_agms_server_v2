package com.example.uxn_api.service.device;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.device.dto.req.DeviceRegistrationDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class DeviceServiceTest {

    private DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private DeviceService deviceService;
    private User user;
    private Long userID;

    @BeforeEach
    public void setUpTest() {
        deviceService = new DeviceService(deviceRepository, userRepository);

        userID = 33L;

        user = User.builder()
            .idx(33L)
            .userName("user")
            .email("user123@gmail.com")
            .password("uxn1234!@")
            .emailVerifiedSuccess(false)
            .build();

    }
    @Test
    @DisplayName("registration test")
    void registrationTest() {
        // given
//        Mockito.when(deviceRepository.save(any(Device.class)))
//                .then(returnsFirstArg());


        DeviceRegistrationDto dto = DeviceRegistrationDto
                .builder()
                .deviceId("deviceID")
                .diabetesLevel(99.9)
                .user_id(userID)
                .build();

        // List 준비
        List<DeviceRegistrationDto> savedata=new ArrayList<>();
        savedata.add(dto);

        // when
//        List<DeviceRegistrationDto> returndata=new ArrayList<>();
//        Long returndata = deviceService.registration(savedata);
//                new DeviceRegistrationDto("deviceID", 99.9, null, userID));
//                new Device(null, user, "deviceID", 99.9));

        // then
//        Assertions.assertEquals(returndata.get(0).getDeviceId(), "deviceID");
//        Assertions.assertEquals(returndata.get(0).getDiabetesLevel(), 99.9);

//        verify(deviceRepository).save(any());


//        User user = userRepository.findByIdx(userID);
//        List<Device> deviceList = deviceRepository.findByUser(user);

    }

    @Test
    @DisplayName("diabetesInfo test")
    void diabetesInfoTest() {
        Long userId = 33L;

//        Device entity = Device.builder()
//                .user(user)
//                .deviceId("device_id")
//                .diabetesLevel(99.9)
//                .build();
//
//        UserInfoResponse uerInfoResponse = new UserInfoResponse(entity);

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
        Mockito.when(userRepository.findByIdx(userID)).thenReturn(user);
        Mockito.when(deviceRepository.findByUser(user)).thenReturn(deviceList);

        List<UserInfoResponse> userInfoResponseList = new ArrayList<>();
//        userInfoResponseList.add(new UserInfoResponse(entity));
//        userInfoResponseList.add(new UserInfoResponse(entity));

        // when
        userInfoResponseList = deviceService.diabetesInfo(userID);

        // then
        Assertions.assertEquals(userInfoResponseList.get(0).getDeviceId(), device.getDeviceId());

//        verify(deviceRepository).findByUser(user);
    }

    @Test
    @DisplayName("diabetesList test")
    void diabetesListTest() {
        String startDay = "2022-12-27 12:00";
        String endDay = "2022-12-30 11:59";
        Long userId = 33L;

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

        // given
        Mockito.when(userRepository.findByIdx(userID)).thenReturn(user);
        Mockito.when(deviceRepository.findByCreateDataTimeBetweenAndUser(
                LocalDateTime.parse(startDay,formatter), LocalDateTime.parse(endDay,formatter), user))
                .thenReturn(deviceList);

        // when
        List<Device> deviceResList = new ArrayList<>();
        deviceResList = deviceService.diabetesList(startDay, endDay, userId);

        // then
        Assertions.assertEquals(deviceResList.get(0).getDeviceId(), device.getDeviceId());
    }

    @Test
    @DisplayName("average test")
    void averageTest() {
        Long idx = 33L;

        Device device = Device.builder()
                .createDataTime(null)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);
        Set<Device> setDevice = new HashSet<>(deviceList);
        user.setDevices(setDevice);

        // given
        Mockito.when(userRepository.findByIdx(idx)).thenReturn(user);

        // when
        OptionalDouble avg = deviceService.average(idx);

        // then
        Assertions.assertEquals(avg.getAsDouble(), device.getDiabetesLevel());

        verify(userRepository).findByIdx(idx);
    }

    @Test
    @DisplayName("activatePercent test")
    void activatePercentTest() {
        Long idx = 33L;

        Device device = Device.builder()
                .createDataTime(null)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);
        Set<Device> setDevice = new HashSet<>(deviceList);
        user.setDevices(setDevice);

        // given
        Mockito.when(userRepository.findByIdx(idx)).thenReturn(user);

        // when
        Double activatePercent = deviceService.activatePercent(idx);

        // then
//        Assertions.assertEquals(activatePercent, 0.0);

        verify(userRepository).findByIdx(idx);
    }

}