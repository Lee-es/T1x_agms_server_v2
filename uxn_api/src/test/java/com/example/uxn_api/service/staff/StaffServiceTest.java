package com.example.uxn_api.service.staff;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.StaffListResDto;
import com.example.uxn_api.web.staff.dto.res.StaffResDto;
import com.example.uxn_api.web.staff.dto.res.UnRecognizeList;
import com.example.uxn_api.web.staff.dto.res.UserInfoResponseDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.email.repository.InviteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.staff.repository.StaffAuthorityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserStaffMapping;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.user.repository.UserStaffMappingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.uxn_common.global.domain.staff.StaffRole.STAFF;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class StaffServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final StaffRepository staffRepository = Mockito.mock(StaffRepository.class);
    private final DeviceRepository deviceRepository = Mockito.mock(DeviceRepository.class);
    private final UserStaffMappingRepository userStaffMappingRepository = Mockito.mock(UserStaffMappingRepository.class);
    private final InviteRepository inviteRepository = Mockito.mock(InviteRepository.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final StaffAuthorityRepository staffAuthorityRepository = Mockito.mock(StaffAuthorityRepository.class);
    private StaffService staffService;

    private Staff staff;
    private User user;

    @BeforeEach
    public void setUpTest() {
        staffService = new StaffService(userRepository, staffRepository, deviceRepository
                ,userStaffMappingRepository, userService, inviteRepository, staffAuthorityRepository);

        staff = Staff.builder()
                .idx(111L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

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
    @DisplayName("deleteStaff test")
    void deleteStaffTest() {
        // given
        Mockito.doNothing().when(inviteRepository).deleteAllByStaff(staff);
        Mockito.doNothing().when(userStaffMappingRepository).deleteAllByStaffId(staff.getIdx());
        Mockito.doNothing().when(staffAuthorityRepository).deleteAll(staff.getAuthorities());
        Mockito.doNothing().when(staffRepository).delete(staff);

        // when
        staffService.deleteStaff(staff);

        // then
        Assertions.assertEquals(staff.getStaffName(), "staff");

        verify(staffRepository).delete(staff);
    }

    @Test
    @DisplayName("addAuthority test")
    void addAuthorityTest() {

        //case1
        Long id = 111L;
        String authority = null;//"ROLE_USER";

        // given
        Mockito.when(staffRepository.save(any(Staff.class)))
                .then(returnsFirstArg());
        Mockito.when(staffRepository.findById(id)).thenReturn(Optional.of(staff));

        // when
        staffService.addAuthority(id, authority);

        // then
        Assertions.assertEquals(staff.getStaffName(), "staff");

        verify(staffRepository).save(staff);

        //case2

        // given
        StaffAuthority staffAuthority = new StaffAuthority(id, "ROLE_STAFF");
        Set<StaffAuthority> staffAuthority2 = new HashSet<>();
        staffAuthority2.add(staffAuthority);

        staff.setAuthorities(staffAuthority2);
        Mockito.when(staffRepository.findById(id)).thenReturn(Optional.of(staff));

        Mockito.when(staffRepository.save(any(Staff.class)))
                .then(returnsFirstArg());

        // when
        staffService.addAuthority(id, authority);

        // then
        Assertions.assertEquals(staff.getStaffName(), "staff");

//        verify(staffRepository).save(staff);

    }

    @Test
    @DisplayName("save test")
    void saveTest(){
        // given
        Mockito.when(staffRepository.save(any(Staff.class)))
                .then(returnsFirstArg());

        Staff staff2 = Staff.builder()
                .idx(33L)
                .staffName("staff")
                .email("staff123@gmail.com")
                .password("uxn1234!@")
                .emailVerifiedSuccess(false)
                .build();

        // when
        Staff staff = staffService.save(staff2);

        // then
        Assertions.assertEquals(staff.getStaffName(), "staff");
        Assertions.assertEquals(staff.getEmail(), "staff123@gmail.com");

        verify(staffRepository).save(any());
    }

    @Test
    @DisplayName("registration1 test")
    void registration1Test(){
        boolean recognize = true;

        UserRegistrationDto regDto = UserRegistrationDto.builder()
                .staffName("staff")
                .userEmail("staff123@gmail.com")
                .staffIdx(111L)
                .userIdx(33L)
                .build();

        // given
        Mockito.when(userRepository.findByEmail(regDto.getUserEmail()))
                .thenReturn(user);

        Mockito.when(staffRepository.findByIdx(regDto.getStaffIdx()))
                .thenReturn(staff);

        Mockito.doNothing().when(userService).appendUserToStaff(user.getIdx(), staff.getIdx(), recognize);
        // when
        staffService.registration(regDto, recognize);
    }

    @Test
    @DisplayName("registration2 test")
    void registration2Test(){
        boolean isRecognize = true;

        Long userID = 33L;
        String email = "staff123@gmail.com";

        //Case1

        // given
        Mockito.when(userRepository.findByIdx(userID)).thenReturn(user);
        Mockito.when(staffRepository.findByEmail(email)).thenReturn(staff);

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(1L);
        mapping.setUserId(user.getIdx());
        mapping.setStaffId(staff.getIdx());

        List<UserStaffMapping> mappingList = new ArrayList<>();
        mappingList.add(mapping);

        Mockito.when(userStaffMappingRepository.findAllByUserId(user.getIdx())).thenReturn(mappingList);
        Mockito.doNothing().when(userService).appendUserToStaff(user.getIdx(), staff.getIdx(), isRecognize);

        // when
        Assertions.assertThrows(RuntimeException.class, () -> {
            staffService.registration(email, userID, isRecognize);
        });

        //Case2
//        mappingList.add(mapping);
//        mappingList.add(mapping);
//        mappingList.add(mapping);
//        mappingList.add(mapping);
//        mappingList.add(mapping);
//
//        // given
//        Mockito.when(userStaffMappingRepository.findAllByUserId(user.getIdx())).thenReturn(mappingList);
//
//        // when
//        Assertions.assertThrows(RuntimeException.class, () -> {
//            staffService.registration(email, userID, isRecognize);
//        });
    }

    @Test
    @DisplayName("checkLink1 test")
    void checkLink1Test(){
        String email = "staff123@gmail.com";
        Long userIdx = 33L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(1L);
        mapping.setUserId(userIdx);
        mapping.setStaffId(111L);

        // given
        Mockito.when(userRepository.findByIdx(userIdx)).thenReturn(user);
        Mockito.when(staffRepository.findByEmail(email)).thenReturn(staff);
        Mockito.when(userStaffMappingRepository.findByUserIdAndStaffId(user.getIdx(), staff.getIdx())).thenReturn(mapping);

        Mockito.when(userStaffMappingRepository.save(any(UserStaffMapping.class)))
                .then(returnsFirstArg());

        // when
        staffService.checkLink(email, userIdx);

        verify(userStaffMappingRepository).save(any());
    }

    @Test
    @DisplayName("checkLink2 test")
    void checkLink2Test(){
        Long idx = 1L;
        Long userID = 33L;
        Long staffID = 111L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(idx);
        mapping.setUserId(userID);
        mapping.setStaffId(staffID);

        // given
        Mockito.when(userRepository.findByIdx(userID)).thenReturn(user);
        Mockito.when(staffRepository.findByIdx(staffID)).thenReturn(staff);
        Mockito.when(userStaffMappingRepository.findByUserIdAndStaffId(user.getIdx(), staff.getIdx())).thenReturn(mapping);

        Mockito.when(userStaffMappingRepository.save(any(UserStaffMapping.class)))
                .then(returnsFirstArg());

        // when
        staffService.checkLink(staffID, userID);

        verify(userStaffMappingRepository).save(any());
    }

    @Test
    @DisplayName("detailStaff test")
    void detailStaffTest(){
        Long staffID = 111L;

        // given
        Mockito.when(staffRepository.findByIdx(staffID)).thenReturn(staff);

        // when
        staffService.detailStaff(staffID);

        verify(staffRepository).findByIdx(any());
    }

    @Test
    @DisplayName("unRecognizeList test")
    void unRecognizeListTest(){
        Long staffID = 111L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(1L);
        mapping.setUserId(user.getIdx());
        mapping.setStaffId(staff.getIdx());

        List<UserStaffMapping> mappingList = new ArrayList<>();
        mappingList.add(mapping);

        List<User> userList = new ArrayList<>();
        userList.add(user);

        List<Long> idList = new ArrayList<>();
        idList.add(mapping.getUserId());

        // given
        Mockito.when(staffRepository.findByIdx(staffID)).thenReturn(staff);
        Mockito.when(userStaffMappingRepository.findAllByStaffIdAndRecognize(staffID, false))
                .thenReturn(mappingList);

        Mockito.when(userRepository.findAllById(idList)).thenReturn(userList);


        List<UnRecognizeList> staffList = new ArrayList<>();

        // when
        staffList = staffService.unRecognizeList(staffID);

        // then
//        Assertions.assertEquals(userInfoResponseList.get(0).getDeviceId(), device.getDeviceId());

        verify(userRepository).findAllById(idList);
    }

    @Test
    @DisplayName("userInfo test")
    void userInfoTest(){
        String userName = "user";

        LocalDateTime createTime = LocalDateTime.now();

        Device device = Device.builder()
                .createDataTime(createTime)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);
        // Set으로 변환 (생성자)
        Set<Device> setDevice = new HashSet<>(deviceList);

        user.setDevices(setDevice);

        // given
        Mockito.when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));

        // when
//        UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(user);
        UserInfoResponseDto userInfoResponseDto = staffService.userInfo(userName);

        // then
        Assertions.assertEquals(userName, userInfoResponseDto.getUserName());

        verify(userRepository).findByUserName(userName);
    }

    @Test
    @DisplayName("staffList test")
    void staffListTest(){
        List<Staff> staffList = new ArrayList<>();
        staffList.add(staff);

        // given
        Mockito.when(staffRepository.findAll()).thenReturn(staffList);

        // when
        List<StaffListResDto> staffListRes = new ArrayList<>();
        staffListRes = staffService.staffList();

        // then
        Assertions.assertEquals(staffList.get(0).getStaffName(), staffListRes.get(0).getStaffName());
        verify(staffRepository).findAll();
    }

    @Test
    @DisplayName("staffDetail test")
    void staffDetailTest(){
        Long staffId = 111L;

        // given
        Mockito.when(staffRepository.findById(staffId)).thenReturn(Optional.of(staff));

        // when
        StaffResDto staffResDto = staffService.staffDetail(staffId);

        // then
        Assertions.assertEquals(staff.getStaffName(), staffResDto.getStaffName());

        verify(staffRepository).findById(staffId);
    }

    @Test
    @DisplayName("staffDetailForUser test")
    void staffDetailForUserTest(){
        Long idx = 33L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(1L);
        mapping.setUserId(user.getIdx());
        mapping.setStaffId(staff.getIdx());

        List<UserStaffMapping> mappingList = new ArrayList<>();
        mappingList.add(mapping);

        List<Long> idList = new ArrayList<>();
        idList.add(mapping.getStaffId());

//        StaffResDto staffResDto = new StaffResDto(staff);
//        List<StaffResDto> StaffResList = new ArrayList<>();
//        StaffResList.add(staffResDto);

        List<Staff> staffList = new ArrayList<>();
        staffList.add(staff);

        // given
        Mockito.when(userRepository.findByIdx(idx)).thenReturn(user);
        Mockito.when(userStaffMappingRepository.findAllByUserId(user.getIdx())).thenReturn(mappingList);
        Mockito.when(staffRepository.findAllById(idList)).thenReturn(staffList);

        // when
        List<StaffResDto> staffResList = new ArrayList<>();
        staffResList = staffService.staffDetailForUser(idx);

        // then
        Assertions.assertEquals(staffList.get(0).getStaffName(), staffResList.get(0).getStaffName());
        verify(staffRepository).findAllById(idList);
    }

    @Test
    @DisplayName("userResult test")
    void userResultTest(){
        String userName = "userName";

        LocalDateTime createTime = LocalDateTime.now();
        Device device = Device.builder()
                .createDataTime(createTime)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceList = Arrays.asList(device);
        Set<Device> setDevice = new HashSet<>(deviceList);

        User reqUser = User.builder()
                .userName("user")
                .email("user123@gmail.com")
                .devices(setDevice)
                .build();

       // given
        Mockito.when(userRepository.findByUserName(userName))
                .thenReturn(Optional.of(reqUser));

        String time = "2022-11-01";
        List<String> groupByDateList = new ArrayList<>();
        groupByDateList.add(time);

        Mockito.when(deviceRepository.customGetDateList(reqUser))
                .thenReturn(groupByDateList);

        // when
        staffService.userResult(userName);
    }

    @Test
    @DisplayName("agpReportDto test")
    void agpReportDtoTest(){
        String name = "name";
        LocalDateTime createTime = LocalDateTime.now();

        String startDay = "2022-12-27 12:00";//createTime.minusDays(2).toString();
        String endDay = "2022-12-30 11:55";//createTime.toString();

        // given
        Mockito.when(userRepository.findByUserName(name))
                .thenReturn(Optional.of(user));

        Device device = Device.builder()
                .createDataTime(createTime)
                .user(user)
                .deviceId("deviceId")
                .diabetesLevel(99.99)
                .build();

        // List 준비
        List<Device> deviceInfoList = Arrays.asList(device);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Mockito.when(deviceRepository.findByCreateDataTimeBetweenAndUser(LocalDateTime.parse(startDay, formatter), LocalDateTime.parse(endDay, formatter), user))
                .thenReturn(deviceInfoList);

        // when
        staffService.agpReportDto(name, startDay, endDay);
    }

    @Test
    @DisplayName("emailVerifyCode test")
    void emailVerifyCodeTest(){
        String code = "verifyCode";
        String email = "staff123@gmail.com";

        // given
        Mockito.when(staffRepository.findByEmail(email))
                .thenReturn(staff);

        // when
        staffService.emailVerifyCode(code, email);
    }

    @Test
    @DisplayName("emailCheck test")
    void emailCheckTest(){
        String email = "staff123@gmail.com";

        // given
        Mockito.when(staffRepository.findByEmail(email))
                .thenReturn(staff);

        // when
        staffService.emailCheck(email);
    }

    @Test
    @DisplayName("deRegistrationUser test")
    void deRegistrationUserTest(){
        Long id = 1L;
        Long userID = 33L;
        Long staffID = 111L;

        UserStaffMapping mapping = new UserStaffMapping();
        mapping.setId(id);
        mapping.setUserId(userID);
        mapping.setStaffId(staffID);

        // given
        Mockito.when(userStaffMappingRepository.findByUserIdAndStaffId(id, staffID))
                .thenReturn(mapping);

        Mockito.doNothing().when(userStaffMappingRepository).delete(mapping);

//        Mockito.when(userStaffMappingRepository.findByUserIdAndStaffId(id, staffID))
//                .then(returnsFirstArg());

        // when
        staffService.deRegistrationUser(id, staffID);

        // then
        Assertions.assertEquals(mapping.getUserId(), userID);
        Assertions.assertEquals(mapping.getStaffId(), staffID);

        verify(userStaffMappingRepository).delete(mapping);
    }

    @Test
    @DisplayName("getUserList1 test")
    void getUserList1Test(){
        Long staffID = 111L;
        int type = 0;
        // when
        staffService.getUserList(staffID, type);

        type = 1;
        // when
        staffService.getUserList(staffID, type);

        type = 2;
        // when
        staffService.getUserList(staffID, type);
    }

    @Test
    @DisplayName("getUserList2 test")
    void getUserList2Test(){
        Long staffID = 111L;
        int type = 0;
        String name = "user";
        String date = "2023-01-03";
        // when
        staffService.getUserList(staffID, type, name, date);

        type = 1;
        // when
        staffService.getUserList(staffID, type, name, date);

        type = 2;
        // when
        staffService.getUserList(staffID, type, name, date);
    }
}