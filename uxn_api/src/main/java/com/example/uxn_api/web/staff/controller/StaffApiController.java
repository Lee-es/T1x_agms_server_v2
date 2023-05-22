package com.example.uxn_api.web.staff.controller;

import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.staff.dto.req.StaffSignUpReqDto;
import com.example.uxn_api.web.staff.dto.req.StaffUpdateReqDto;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.*;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.utils.annotation.Timer;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staff")
@Slf4j
@EnableAsync
public class StaffApiController {

    private final StaffService staffService;

    private final MailSendService mailSendService;



    @PutMapping("/registration/user")
    @ApiOperation(value = "의사가 직접 환자 등록", notes = "410: 승인코드를 입력해주세요. 411: 병원 정보를 찾을 수 없습니다. 412:승인코드를 확인해주세요.")
    public ResponseEntity<UserRegistrationDto> staffRegistrationUser(@RequestBody UserRegistrationDto registrationDto){
        log.debug("registration user start:"+registrationDto.getCode());
        Staff staff = staffService.detailStaff(registrationDto.getStaffIdx());
//        staffService.plusUserCount(registrationDto.getStaffIdx());
        if(registrationDto.getCode()==null || registrationDto.getCode().isBlank()){
            log.debug("staffRegistrationUser: code empty");
            throw new LoginException("code empty", ErrorCode.INPUT_CODE_EMPTY);
        }
        if(staff==null){
            log.debug("staffRegistrationUser: staff not found");
            throw new LoginException("STAFF_NOT_FOUND", ErrorCode.STAFF_NOT_FOUND);
        }
        if(!staff.getApprovalCode().equals(registrationDto.getCode())){
            log.debug("staffRegistrationUser: not equal approval code");
            throw new LoginException("CODE_NOT_EQUAL", ErrorCode.CODE_NOT_EQUAL);
        }
        return ResponseEntity.ok(staffService.registration(registrationDto,true));
    }

    @GetMapping("/user-detail/{userName}")
    @ApiOperation(value = "환자 상세 조회", notes = "환자 리스트에서 환자 클릭시 환자 상세 데이터 조회")
    public ResponseEntity<UserInfoResponseDto> userDetail(@PathVariable String userName){
        return ResponseEntity.ok().body(staffService.userInfo(userName));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "의사 상세 조회")
    public ResponseEntity<StaffResDto> staffDetail(@PathVariable Long id){
        return ResponseEntity.ok(staffService.staffDetail(id));
    }

    @GetMapping("/user-list/{id}")
    @Timer
    @ApiOperation(value = "유저 리스트 조회", notes = "유저의 상세 데이터 리스트")
    public ResponseEntity<List<UserListForStaff>> userList(@PathVariable Long id, @RequestParam(required = false) String name, @RequestParam(required = false) String date){
        Staff staff = staffService.detailStaff(id);
//        List<User> userList = staff.getUserList();

//        List<String> userInfoList = staffService.getUserInfoList(staff.getIdx(),1);
        Map<Long, String> userInfoList = staffService.getUserInfoList(staff.getIdx(),1);
//        List<User> userList = staffService.getUserList(staff.getIdx(),1,name,date);

        List<Double> diabetesList;
        List<UserListForStaff> resultList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        //년월(date) 기준 직전 6개월 데이터 가져오기 테스트 - 2023.02.03, ykw
//        List<String> dateList = new ArrayList<>();
//        if(date)
//        dateList.add(date);
//        dateList.add(date);
//        dateList.add(date);
//        dateList.add(date);

        log.info("list date : "+date);
//        for(User user : userList){
//        for(String userInfo : userInfoList){
        for ( Long key : userInfoList.keySet()){

            Long userID = key;
            String userInfo = userInfoList.get(key);


//            diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
////            if(StringUtils.hasText(date)){
////
////                diabetesList = user.getDevices().stream()
////                        .filter(item->{
////                            return item.getCreateDataTime() != null && item.getCreateDataTime().format(formatter).equalsIgnoreCase(date);
////                        })
////                        .map(Device::getDiabetesLevel).collect(Collectors.toList());
////            } else {
////                diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
////            }
//
//            if(StringUtils.hasText(date)){
//                if(diabetesList == null || diabetesList.isEmpty()){
//                    continue;
//                }
//
//            }
//
//            //평균 구하기 수정 - 2023.02.06, ykw
//            //기존 : sum으로 모두 더해서 사이즈로 나누기
//            //변경 : average() 함수 사용 후 반올림
//            double size = diabetesList.size();
////            double sum = diabetesList.stream().mapToDouble(Double::doubleValue).sum();
//
//            OptionalDouble avg = diabetesList.stream().mapToDouble(Double::doubleValue).average();
//
//            Double average;
//            LocalDateTime lastData;
//            String deviceID="";
//
//            log.info("size : {}", size);
//
//            int underCount =  (int) ((diabetesList.stream().filter(s -> s < 70).count()) * 100);
//            int goalCount =  (int) ((diabetesList.stream().filter(s -> 70 <= s && s <= 180).count()) * 100);
//            int excessCount =  (int) ((diabetesList.stream().filter(s -> s > 180).count()) * 100);
//
//            if (size == 0) {
//                log.info("size0 ? : {}", size);
//                average = null;
//                lastData = null;
//
//            }else {
//                log.info("size : {}", size);
////                average = Math.ceil(sum / size)*100/100;
//                average = (double)Math.round(avg.getAsDouble() * 100) / 100.0;
//                lastData = user.getDevices().stream().map(Device::getCreateDataTime).collect(Collectors.toList()).get((int) (size-1));
//            }
//
//            //device id - 2023.02.08, ykw
//            deviceID = user.getDevices().stream().map(Device::getDeviceId).collect(Collectors.toList()).get((int) (size-1));

            String[] userInfo2 = userInfo.split(",");

            UserListForStaff result = UserListForStaff
                    .builder()
                    .userIdx(userID)
                    .userName(userInfo2[0])
                    .userEmail(userInfo2[1])
                    .userBirth(userInfo2[2])
                    .userGender(userInfo2[3])
                    .emailVerifyDate(userInfo2[4])
                    .deviceID("deviceID")
//                    .lastDataTime(lastData)
//                    .average(average)
                    .scanAverage(7)
//                    .underPercent(Math.round(underCount/size)*100/100)
//                    .goalPercent(Math.round(goalCount/size)*100/100)
//                    .excessPercent(Math.round(excessCount/size)*100/100)
                    .build();
            resultList.add(result);
        }

        return ResponseEntity.ok().body(resultList);

    }

    @GetMapping("/user-list/unreconize/{id}")
    @Timer
    @ApiOperation(value = "승인안된 유저 리스트 조회", notes = "유저의 상세 데이터 리스트")
    public ResponseEntity<List<UserListForStaff>> unReconizeUserList(@PathVariable Long id){
        Staff staff = staffService.detailStaff(id);
//        List<User> userList = staff.getUserList();

        List<User> userList = staffService.getUserList(staff.getIdx(),0);
        List<Double> diabetesList;
        List<UserListForStaff> resultList = new ArrayList<>();

        for(User user : userList){
            diabetesList = user.getDevices().stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
            double size = diabetesList.size();
            double sum = diabetesList.stream().mapToDouble(Double::doubleValue).sum();
            Double average;
            String deviceID="";
            LocalDateTime lastData;
            log.info("size : {}", size);

            int underCount =  (int) ((diabetesList.stream().filter(s -> s < 70).count()) * 100);
            int goalCount =  (int) ((diabetesList.stream().filter(s -> 70 <= s && s <= 180).count()) * 100);
            int excessCount =  (int) ((diabetesList.stream().filter(s -> s > 180).count()) * 100);

            if (size == 0) {
                log.info("size0 ? : {}", size);
                average = null;
                lastData = null;

            }else {
                log.info("size : {}", size);
                average = Math.ceil(sum / size)*100/100;

                lastData = user.getDevices().stream().map(Device::getCreateDataTime).collect(Collectors.toList()).get((int) (size-1));
            }

            //device id - 2023.02.08, ykw
            deviceID = user.getDevices().stream().map(Device::getDeviceId).collect(Collectors.toList()).get((int) (size-1));

            UserListForStaff result = UserListForStaff
                    .builder()
                    .userIdx(user.getIdx())
                    .userName(user.getUsername())
                    .userBirth(user.getBirth())
                    .userEmail(user.getEmail())
                    .deviceID(deviceID)
                    .lastDataTime(lastData)
                    .average(average)
                    .scanAverage(7)
                    .underPercent(Math.round(underCount/size)*100/100)
                    .goalPercent(Math.round(goalCount/size)*100/100)
                    .excessPercent(Math.round(excessCount/size)*100/100)
                    .build();
            resultList.add(result);
        }

        return ResponseEntity.ok().body(resultList);

    }

    //사용하지 않는 함수 - 22.12.29 ykw
//    @PutMapping("/de-registration")
//    @ApiOperation(value = "환자 등록해제", notes = "진단이 완료 된 환자 등록해제")
//    @Async
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "staffId", value = "의사 index", required = true)
//            , @ApiImplicitParam(name = "userId", value = "환자 index", required = true)
//    }
//    )
//    public void userDeRegistration(@RequestParam Long staffId, @RequestParam Long userId){
//        staffService.minusUserCount(staffId);
//        staffService.deRegistrationUser(userId, staffId);
//    }

    @GetMapping("/search/{userName}")
    @ApiOperation(value = "환자 이름검색")
    public ResponseEntity<UserListForStaff> searchUserName(
            @PathVariable String userName
    ) {
        return ResponseEntity.ok().body(staffService.userResult(userName));
    }

    @GetMapping("/snap-shot/{userName}")
    @ApiOperation(value = "스냅샷")
    public ResponseEntity<UserListForStaff> snapShot(
            @PathVariable String userName
    ) {
        return ResponseEntity.ok().body(staffService.userResult(userName));
    }

    @GetMapping("/agp-report/{userName}")
    @ApiOperation(value = "데이터 리스트")
    public ResponseEntity<AgpReportDto> agpReport(
            @PathVariable String userName,
            @RequestParam String startDay,
            @RequestParam String endDay
    ) {
        return ResponseEntity.ok(staffService.agpReportDto(userName, startDay, endDay));
    }

//    @GetMapping("/excelDownload/{userEmail}")
//    public ResponseEntity<String> excelDownload(
//            @PathVariable String userEmail
//    ) {
//        List<Device> deviceList = staffService.findAllByUserListDevice(userEmail);
//        HttpHeaders header = new HttpHeaders();
//        header.add("Content-Type", "text/csv; charset=UTF-8");
//        header.add("Content-Disposition", "attachment; filename=\""+"user.csv"+"\"");
//
//
//        return new ResponseEntity<String>(setContent(deviceList), header, HttpStatus.CREATED);
//    }
//
//    public String setContent(List<Device> deviceList) {
//        StringBuilder data = new StringBuilder();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        data.append("UUID, 이름, 수치, 데이터기준일 : ").append(sdf.format(new Date())).append("\n");
//        for (Device device : deviceList) {
//            data.append(device.getDeviceId()).append(",");
//            data.append(device.getUser().getUsername()).append(",");
//            data.append(device.getDiabetesLevel()).append("\n");
//        }
//
//        return data.toString();
//    }


    // 의사가 환자의 이메일을 입력해서 링크를 전달해줌.
    /*@GetMapping("/send-mail/to-user/{staffIdx}")
    @ApiOperation(value = "병원 등록 링크", notes = "의사가 환자의 이메일을 입력해서 링크를 전달해줌.")
    public void sendMailToUser(
            @PathVariable Long staffIdx,
            String userEmail
    ) throws MessagingException {
        mailSendService.verificationMailSend(userEmail, 4, staffIdx, );
    }*/

//    @GetMapping("/user-link/{id}/{email}")
//    public void userLinkCheck(
//            @PathVariable Long id,
//            @PathVariable String email
//    ) {
//
////        staffService.userLink(email,id);
//    }

    @PostMapping("/update")
    @ApiOperation(value = "의사 정보 수정 ")
    public ResponseEntity<Long> signUp(@RequestBody StaffUpdateReqDto reqDto){
        Staff chkStaff = staffService.findByEmail(reqDto.getEmail());
        if(chkStaff != null){
            chkStaff.setStaffName(reqDto.getStaffName());
            chkStaff.setEmail(reqDto.getEmail());
            chkStaff.setHospital(reqDto.getHospital());
            chkStaff.setBirth(reqDto.getBirth());
            chkStaff.setPhoneNumber(reqDto.getPhoneNumber());
            chkStaff.setGender(reqDto.getGender());
            chkStaff.setApprovalCode(reqDto.getApprovalCode());

            staffService.save(chkStaff);
            return ResponseEntity.ok().body(chkStaff.getIdx());
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
