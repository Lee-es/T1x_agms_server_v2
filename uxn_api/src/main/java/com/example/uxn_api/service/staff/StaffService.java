package com.example.uxn_api.service.staff;

import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.device.dto.res.DeviceForAgpReportDto;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.*;
import com.example.uxn_api.web.staff.dto.res.UnRecognizeList;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.email.repository.InviteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.staff.repository.StaffAuthorityRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserStaffMapping;
import com.example.uxn_common.global.domain.user.repository.UserRepository;

import com.example.uxn_common.global.domain.user.repository.UserStaffMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {


    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final DeviceRepository deviceRepository;
    private final UserStaffMappingRepository userStaffMappingRepository;

    private final UserService userService;

    private final InviteRepository inviteRepository;

    private final StaffAuthorityRepository staffAuthorityRepository;
    private static final Integer MAX_COUNT = 5;

    @Transactional
    public void deleteStaff(Staff staff){
        try{
            inviteRepository.deleteAllByStaff(staff);
            inviteRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            userStaffMappingRepository.deleteAllByStaffId(staff.getIdx());
            userStaffMappingRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            staffAuthorityRepository.deleteAll(staff.getAuthorities());
            staffAuthorityRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        staffRepository.delete(staff);
        staffRepository.flush();
    }
    @Transactional
    public void addAuthority(Long id, String authority){
        staffRepository.findById(id).ifPresent(user -> {
            StaffAuthority newRole = new StaffAuthority(user.getIdx(), authority);
            if(user.getAuthorities() == null){
                HashSet<StaffAuthority> authorities = new HashSet<>();
                authorities.add(newRole);
                user.setAuthorities(authorities);
//                User.builder().authorities(user.getAuthorities());
                save(user);
            }else if(!user.getAuthorities().contains(newRole)){
                HashSet<StaffAuthority> authorities = new HashSet<>();
                authorities.addAll(user.getAuthorities());
                authorities.add(newRole);
                user.setAuthorities(authorities);
//                User.builder().authorities(user.getAuthorities());
                save(user);
            }
        });
    }

    @Transactional
    public Staff save(Staff staff){
         return staffRepository.save(staff);
    }

    @Transactional
    public UserRegistrationDto registration(UserRegistrationDto requestDto, boolean recognize){
        User user = userRepository.findByEmail(requestDto.getUserEmail());
        Staff staff = staffRepository.findByIdx(requestDto.getStaffIdx());

        List<UserStaffMapping> mappingList   =userStaffMappingRepository.findAllByUserId(user.getIdx());

        Staff before = null;
        if(mappingList!=null){
            for(UserStaffMapping mapping : mappingList){
                if(mapping.getStaffId() == staff.getIdx()){
                    throw new LoginException("error", ErrorCode.DUPLICATE_STAFF);
                }
            }
            if(mappingList.size()>=5){
                throw new LoginException("error", ErrorCode.OVER_5_STAFF);
            }
        }
        userService.appendUserToStaff(user.getIdx(), staff.getIdx(),recognize);

        return requestDto;
    }

    /*@Transactional
    public void registrationWithEmailAndIndex(String email, Long idx) {
        User user = userRepository.findByIdx(idx);
        Staff staff = staffRepository.findByEmail(email);
//        user.update(staff);

    }*/

    @Transactional
    public void registration(String email, Long idx,boolean isRecozition) {
        User user = userRepository.findByIdx(idx);
        Staff staff = staffRepository.findByEmail(email);
        //user.update(staff);
        log.debug("registration start");
        List<UserStaffMapping> mappingList   = userStaffMappingRepository.findAllByUserId(user.getIdx());
        if(mappingList!=null){
            for(UserStaffMapping mapping : mappingList){
                if(mapping.getStaffId() == staff.getIdx()){
                    log.debug("registration DUPLICATE_STAFF");
                    throw new LoginException("error", ErrorCode.DUPLICATE_STAFF);
                }
            }
            if(mappingList.size()>=5){
                log.debug("registration OVER_5_STAFF");
                throw new LoginException("error", ErrorCode.OVER_5_STAFF);
            }
        }
        userService.appendUserToStaff(user.getIdx(), staff.getIdx(),isRecozition);
        log.debug("registration end");
    }

    @Transactional
    public String checkLink(String email, Long idx) {
        //환자의 병원 등록 요청 이메일을 보고 등록 승인을 하는 과정
        User user = userRepository.findByIdx(idx);
        Staff staff = staffRepository.findByEmail(email);
        if(user==null){
            return "사용자를 찾을 수 없습니다.";
        }
        if(staff == null){
            return "병원 정보를 찾을 수 없습니다.";
        }
        UserStaffMapping mapping = userStaffMappingRepository.findByUserIdAndStaffId(user.getIdx(),staff.getIdx());
        if(mapping==null){
            return "등록 정보를 찾을 수 없습니다.";
        }
        if(mapping.isRecognize()){
            return "이미 승인되었습니다.";
        }
        mapping.setRecognize(true);
        userStaffMappingRepository.save(mapping);
        return null;
    }

    @Transactional
    public void checkLink(Long staffId, Long userIdx) {
        User user = userRepository.findByIdx(userIdx);
        Staff staff = staffRepository.findByIdx(staffId);
        if(user==null){
            throw new LoginException("user not found",ErrorCode.USER_NOT_FOUND);
        }
        if(staff == null){
            throw new LoginException("staff not found",ErrorCode.STAFF_NOT_FOUND);
        }
        UserStaffMapping mapping = userStaffMappingRepository.findByUserIdAndStaffId(user.getIdx(),staff.getIdx());
        if(mapping==null){
            throw new LoginException("target not found",ErrorCode.TARGET_NOT_FOUND);
        }
        if(mapping.isRecognize()){
            throw new LoginException("recognized before",ErrorCode.BEFORE_DONE);
        }
        mapping.setRecognize(true);
        userStaffMappingRepository.save(mapping);
    }

    /*@Transactional
    public void recognize(Long idx){
        User user = userRepository.findByIdx(idx);
        user.updateRecognize(true);
    }*/


    /*
    public void userLink(String email, Long idx) {
        User user = userRepository.findByEmail(email);
        Staff staff = staffRepository.findByIdx(idx);
        if(user==null){
            throw new LoginException("user not found",ErrorCode.USER_NOT_FOUND);
        }
        if(staff == null){
            throw new LoginException("staff not found",ErrorCode.STAFF_NOT_FOUND);
        }
    }*/

    // R
    @Transactional(readOnly = true)
    public Staff detailStaff(Long id){
        return staffRepository.findByIdx(id);
    }


    public List<UnRecognizeList> unRecognizeList (Long id) { // 승인이 안된 유저 리스트
        Staff staff = staffRepository.findByIdx(id);
        List<UserStaffMapping> list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staff.getIdx(),false);
        List<Long> idList = new ArrayList<>();
        if(list!=null){
            for(UserStaffMapping mapping:list){
                idList.add(mapping.getUserId());
            }
        }
        List<User> userList = userRepository.findAllById(idList);
        return userList.stream().map(UnRecognizeList::new).collect(Collectors.toList());
    }

    /*
    @Transactional(readOnly = true)
    public void userListDetail(Long id){
        Staff staff = staffRepository.findById(id).orElseThrow();
        List<User> userList = staff.getUserList();
        List<Set<Device>> deviceList = userList.stream().map(User::getDevices).collect(Collectors.toList());


    }*/

    @Transactional(readOnly = true)
    public UserInfoResponseDto userInfo(String userName){
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음."));
        return new UserInfoResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<StaffListResDto> staffList(){
        return staffRepository.findAll().stream().map(StaffListResDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StaffResDto staffDetail(Long id){
        Staff staff = staffRepository.findById(id).orElseThrow();
        return new StaffResDto(staff);
    }

    @Transactional(readOnly = true)
    public List<StaffResDto> staffDetailForUser(Long id) {
        User user = userRepository.findByIdx(id);
        List<UserStaffMapping> list = userStaffMappingRepository.findAllByUserId(user.getIdx());
        if(list!=null){
            List<Long> idList = new ArrayList<>();
            HashMap<Long,Boolean> reconizeMap = new HashMap<>();
            for(UserStaffMapping mapping : list){
                idList.add(mapping.getStaffId());
                reconizeMap.put(mapping.getStaffId(), mapping.isRecognize());
            }
            List<StaffResDto> tmp =  staffRepository.findAllById(idList).stream().map(StaffResDto::new).collect(Collectors.toList());
            if(tmp!=null){
                for(StaffResDto dto : tmp){
                    try {
                        dto.setRecognize(reconizeMap.get(dto.getIdx()));    
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    
                }
            }
            return tmp;
        }

        return null;
    }

    /*
    @Transactional(readOnly = true)
    public UserListDto userListDto(Long id){
        Staff staff = staffRepository.findById(id).orElseThrow();
        List<UserInfoList> userList;
        UserListDto dto = new UserListDto();
        return dto;
    }*/

    @Transactional(readOnly = true)
    public Staff findByEmail(String email){
        return staffRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Staff findByIdx(Long id){
        return staffRepository.findByIdx(id);
    }

    @Transactional(readOnly = true)
    public UserListForStaff userResult(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow();
        List<Double> diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
        Set<Device> devices = user.getDevices();
        int size = diabetesList.size();
        double sum = diabetesList.stream().mapToDouble(Double::doubleValue).sum();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Double average;
        int underPercent;
        int goalPercent;
        int excessPercent;
        String deviceID="";
        LocalDateTime lastData;
        List<String> groupByDateList = deviceRepository.customGetDateList(user); // 날짜 그룹
//        List<Device> low = devices.stream().filter(s -> s.getDiabetesLevel() < 70).collect(Collectors.toList());
        List<LowBloodSugarDto> lowBloodSugarDtoList = devices.stream().filter(s -> s.getDiabetesLevel() < 70).map(LowBloodSugarDto::new).collect(Collectors.toList());
        ArrayList<Double> dataList = new ArrayList<>();
        ArrayList<String> timeList = new ArrayList<>();

        HashMap<String, List<Double>> dataMap = new HashMap<>();
        List<TimeActivate> timeActivateList = new ArrayList<>();
        List<ActivateGraphDto> activateGraphDtoList = new ArrayList<>();

        for(Device device : devices){
            String key = device.getCreateDataTime().format(timeFormat);
            String dateString = device.getCreateDataTime().format(dateFormat);
            List<Double> inner = dataMap.get(key);
            if(inner == null) {
                inner = new ArrayList<>();
            }
            inner.add(device.getDiabetesLevel());
            dataMap.put(key, inner);
        }

        for (String key : dataMap.keySet().stream().sorted().collect(Collectors.toList())) {
            TimeActivate activate = TimeActivate
                    .builder()
                    .time(key)
                    .count(dataMap.get(key).size())
                    .activatePercent((double) (dataMap.get(key).size()*100/groupByDateList.size()))
                    .build();
            timeActivateList.add(activate);
        }

        if (size != 0) {
            average = sum / size;
            underPercent =  (int) ((diabetesList.stream().filter(s -> s < 70).count()) * 100)/size;
            goalPercent =  (int) ((diabetesList.stream().filter(s -> 70 <= s && s <= 180).count()) * 100)/size;
            excessPercent =  (int) ((diabetesList.stream().filter(s -> s > 180).count()) * 100)/size;
            lastData = user.getDevices().stream().map(Device::getCreateDataTime).collect(Collectors.toList()).get(size-1);
        } else {
            average = null;
            underPercent =  0;
            goalPercent =  0;
            excessPercent =  0;
            lastData = null;
        }

        deviceID = user.getDevices().stream().map(Device::getDeviceId).collect(Collectors.toList()).get((int) (size-1));

        Long lowBloodSugarCount = diabetesList.stream().filter(s -> s < 70).count();
        int diabetesCount = (int) user.getDevices().stream().map(Device::getDiabetesLevel).count(); // 유저의 전체 데이터
        double activatePercent = 0;
        activatePercent = (diabetesCount/20160) * 100;

//        if (day != null) {
//            int denominator = day * 720;
//            activatePercent = (diabetesCount / denominator) * 100;
//        } else {
//            activatePercent = (diabetesCount/20160) * 100;
//        }

        return UserListForStaff
                .builder()
                .userIdx(user.getIdx())
                .userName(user.getUsername()) // 유저 이름
                .userBirth(user.getBirth()) // 생년월일
                .userEmail(user.getEmail())// 유저 이메일
                .lastDataTime(lastData) // 마지막으로 받은 데이터
                .average((double) (Math.round(average)*100/100)) // 평균 혈당
                .scanAverage(user.getScanCount()) // 이거는 어떻게 조회?
                .underPercent(Math.ceil(underPercent)*100/100) // 목표값 미만
                .goalPercent(Math.ceil(goalPercent)*100/100) // 목표값
                .excessPercent(Math.ceil(excessPercent)*100/100) // 목표값 초과
                .lowBloodSugarCount(lowBloodSugarCount) // 저혈당 카운트
                .timeSensorActivate(activatePercent) // 활성화 퍼센트
                .lowBloodSugarDuration(lowBloodSugarCount) // 저혈당 평균 지속 시간
                .lowBloodSugarDtoList(lowBloodSugarDtoList)
                .timeActivateList(timeActivateList)
                .build();
    }

    @Transactional(readOnly = true)
    public AgpReportDto agpReportDto(String name, String startDay, String endDay) {
        User user = userRepository.findByUserName(name).orElseThrow();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Device> deviceInfoList = deviceRepository.findByCreateDataTimeBetweenAndUser(LocalDateTime.parse(startDay, formatter), LocalDateTime.parse(endDay, formatter), user);
        List<DeviceForAgpReportDto> deviceForAgpReportDtos = new ArrayList<>();


        for (Device device : deviceInfoList) {
            DeviceForAgpReportDto deviceForAgpReportDto = new DeviceForAgpReportDto(device);
            deviceForAgpReportDtos.add(deviceForAgpReportDto);
        }

        for(DeviceForAgpReportDto list : deviceForAgpReportDtos) {
        }

        return AgpReportDto
                .builder()
                .deviceInfoList(deviceForAgpReportDtos)
                .build();

    }

    public List<Device> findAllByUserListDevice(String email) {
        User user = userRepository.findByEmail(email);
        return deviceRepository.findAllByUser(user);
    }

    //사용하지 않아서 주석처리 - 22.12.29 ykw
//    @Transactional
//    public void plusUserCount(Long id){
//        staffRepository.updatePlusUserCount(id);
//    }
//
//    @Transactional
//    public void plusCountWithEmail(String email){
//        staffRepository.updatePlusUserCountWithEmail(email);
//    }

    @Transactional
    public void emailVerifyCode(String code, String email){
        Staff staff = staffRepository.findByEmail(email);
        staff.setEmailVerifyCode(code);
        staff.setEmailVerifyStartTime(LocalDateTime.now());
    }

    @Transactional
    public void emailCheck(String email){
        Staff staff = staffRepository.findByEmail(email);
        staff.emailCheck(true);

    }

    @Transactional
    public void minusUserCount(Long id){
//        Staff staff = staffRepository.findById(id).orElseThrow();
//        if(staff.getUserCount() <= 0){
//            return 0;
//        }else {
//            return staffRepository.updateMinusUserCount(id);
//        }
        staffRepository.updateMinusUserCount(id);
    }

    @Transactional
    public void deRegistrationUser(Long id, Long staffId){

        UserStaffMapping mapping = userStaffMappingRepository.findByUserIdAndStaffId(id,staffId);
        if(mapping!=null){
            userStaffMappingRepository.delete(mapping);
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     *
     * @param staffId
     * @param type 1: 승인된 사람만, 2:전부, 0: 승인 안된 사람
     * @return
     */
    public List<User> getUserList(long staffId, int type){
        List<UserStaffMapping> list = null;
        if(type == 0){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,false);
        } else if(type == 1){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,true);
        } else {
            list = userStaffMappingRepository.findAllByStaffId(staffId);
        }
        if(list!=null){
            List<Long> userId = new ArrayList<>();
            for(UserStaffMapping mapping : list){
                userId.add(mapping.getUserId());
            }
            return userRepository.findAllById(userId);
        }
        return null;
    }

    public List<User> getUserList(long staffId, int type, String name, String date){
        List<UserStaffMapping> list = null;
        if(type == 0){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,false);

        } else if(type == 1){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,true);
        } else {
            list = userStaffMappingRepository.findAllByStaffId(staffId);
        }
        if(list!=null){
            List<Long> userId = new ArrayList<>();
            for(UserStaffMapping mapping : list){
                userId.add(mapping.getUserId());
            }
            if(StringUtils.hasText(name)){
                return userRepository.findAllByIdxInAndUserNameLike(userId, "%"+name+"%");
            } else {
                return userRepository.findAllById(userId);
            }

        }
        return null;
    }

    public Map<Long, String> getUserInfoList(long staffId, int type){
        List<UserStaffMapping> list = null;
        if(type == 0){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,false);

        } else if(type == 1){
            list = userStaffMappingRepository.findAllByStaffIdAndRecognize(staffId,true);
        } else {
            list = userStaffMappingRepository.findAllByStaffId(staffId);
        }
        if(list!=null){
//            List<Long> userId = new ArrayList<>();
//            String userName = null;

            String userInfo = null;
            Map<Long, String> userInfoList = new LinkedHashMap<>();

            for(UserStaffMapping mapping : list){
                Long userID = mapping.getUserId();
                userInfo = userRepository.getUserNameByUserID(userID);
                String verifyTime = userRepository.getUserVerifyTimeByUserID(userID).toLocalDate().toString();
                userInfo = userInfo + "," + verifyTime;

                userInfoList.put(userID, userInfo);

//                userId.add(mapping.getUserId());
            }

            return userInfoList;

//            if(StringUtils.hasText(name)){
//                return userRepository.findAllByIdxInAndUserNameLike(userId, "%"+name+"%");
//            } else {
//                return userRepository.findAllById(userId);
//            }

        }
        return null;
    }





}
