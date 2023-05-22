package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.device.DeviceService;
import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.service.login.UserLoginService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.device.dto.res.*;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_api.web.login.dto.res.LoginCheckResult;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.utils.annotation.Timer;
import com.example.uxn_common.global.utils.excel.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/info")
@Slf4j
public class UserInfoApiController {

    private final UserService userService;

    private final DeviceService deviceService;



    @GetMapping("/{id}")
    @Timer
    @ApiOperation(value = "유저 상세 정보")
    public ResponseEntity<UserInfoResDto> getUserInfo(@PathVariable Long id){

        return ResponseEntity.ok(userService.findByIdx(id));
    }

    @GetMapping("/search/{userId}")
    @Timer
    public ResponseEntity<UserInfoResDto> getUserInfoByUserId(@PathVariable String userId){
        return ResponseEntity.ok(userService.findByUserId(userId));
    }

    @GetMapping("/median/{id}")
    @Timer
    public ResponseEntity<Double> getMedian(
            @PathVariable Long id,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day
    ){ // 중간값 : 해당 일자의 전체데이터의 중간 값 혈당 구하는 로직 추가 -> 파라미터가 들어오면 해당 일자의 데이터의 중간값 을 구하고 안들어 오면 전체 데이터
        UserInfoResDto userInfo = userService.findByIdx(id);
        List<CheckDeviceResponse> deviceList = userInfo.getCheckDevices();
        List<Double> diabetesLevelList = new ArrayList<>();
        for(CheckDeviceResponse list : deviceList) {
            if (month == list.getCreateDataTime().getMonthValue()) {
                if (day == list.getCreateDataTime().getDayOfMonth()) {
                    Double diabetes = list.getDiabetesLevel();
                    diabetesLevelList.add(diabetes);
                }
            }
        }
        ExcelUtils excelUtils = new ExcelUtils();

        Double median = excelUtils.medianListFunction(diabetesLevelList);

        return ResponseEntity.ok().body(median);
    }
    @GetMapping("/percentile/time/{id}")
    @ApiOperation(value = "특정 시간대의 14일치 백분율")
    @Timer
    public ResponseEntity<Map<String, ArrayList>> getPercentileWithTime ( // 00시 00분 을 파라미터로 받을게 아니라 포문을 돌면서 시간에 대한 데이터를 전부다 리스트로 내려줘야함.
            @PathVariable Long id,
            @RequestParam(required = false) Integer hour,
            @RequestParam(required = false) Integer minutes) {
        UserInfoResDto userInfo = userService.findByIdx(id);
        List<CheckDeviceResponse> deviceList = userInfo.getCheckDevices();
        ArrayList<Double> diabetesList = new ArrayList<>();

        for(CheckDeviceResponse list : deviceList) {
            int listHour = list.getCreateDataTime().getHour();
            int listMinutes = list.getCreateDataTime().getMinute();
            if(hour == listHour) {
                if (minutes == listMinutes) {
                    diabetesList.add(list.getDiabetesLevel());
                }
            }
        }
        ExcelUtils excelUtils = new ExcelUtils();

        // 일단 서버에서 전부 계산해서 줄 경우
        HashMap<String, ArrayList> map = new HashMap<>();
        double five = diabetesList.size() * 0.005; // 5% -> 0.7
        double twentyFive = diabetesList.size() * 0.25; //25% -> 3.5
        double fifty = diabetesList.size() * 0.5; // 50% -> 7
        double seventyFive = diabetesList.size() * 0.75; // 75% -> 10.5
        double ninetyFive = diabetesList.size() * 0.95; // 95% -> 13.3

        // 배열값 하나가 들어올 경우 여기서 0이 됨. -> smallListFunction 에서 out of index 발생 -> 핸들링 코드 필요. -> 현재 배열의 인자가 하나일 경우 그 하나를 리턴. -> 엑셀 술식대로 라면 해당 데이터 자체가 리턴 돼야함.
        int first = excelUtils.intFunction(five);
        int second = excelUtils.intFunction(twentyFive);
        int third = excelUtils.intFunction(fifty);
        int fourth = excelUtils.intFunction(seventyFive);
        int fifth = excelUtils.intFunction(ninetyFive);

        int[] percentile1 = new int[] {first+1};
        int[] percentile2 = new int[] {second+1};
        int[] percentile3 = new int[] {third};
        int[] percentile3_1 = new int[] {third+1};
        int[] percentile4 = new int[] {fourth+1};
        int[] percentile5 = new int[] {fifth+1};

        ArrayList<Double> list1 = excelUtils.smallListFunction(diabetesList,percentile3);
        ArrayList<Double> list2 = excelUtils.smallListFunction(diabetesList,percentile3_1);

        double fiftyRound = excelUtils.roundFunction((list1.get(0)+list2.get(0))/2.0 , 0); // 나눗셈시 분자 분모가 둘다 정수면 정수 나누기가 되므로 소숫점은 자동 내림됨.

        map.put("5%", excelUtils.smallListFunction(diabetesList, percentile1));
        map.put("25%", excelUtils.smallListFunction(diabetesList, percentile2));
        map.put("50%", excelUtils.returnListRoundFunction(fiftyRound, 0));
        map.put("75%", excelUtils.smallListFunction(diabetesList, percentile4));
        map.put("95%", excelUtils.smallListFunction(diabetesList, percentile5));
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/percent/{id}")
    @ApiOperation(value = " 전체 데이터의 백분율 api")
    public ResponseEntity<Map<String, ArrayList>> getPercent ( // start 와 end 데이터를 받아서 리턴 해줘야됨.
            @PathVariable Long id
            ) { // 전체 데이터의 백분율 값.
        UserInfoResDto userInfo = userService.findByIdx(id);
        List<CheckDeviceResponse> deviceList = userInfo.getCheckDevices();
        List<Double> diabetesList = deviceList.stream().map(CheckDeviceResponse::getDiabetesLevel).collect(Collectors.toList());
        log.info("size : {}", diabetesList.size());
        ExcelUtils excelUtils = new ExcelUtils();

        // 일단 서버에서 전부 계산해서 줄 경우
        HashMap<String, ArrayList> map = new HashMap<>();
        double five = diabetesList.size() * 0.005; // 5%
        double twentyFive = diabetesList.size() * 0.25; //25%
        double fifty = diabetesList.size() * 0.5; // 50% -> 7
        double seventyFive = diabetesList.size() * 0.75; // 75%
        double ninetyFive = diabetesList.size() * 0.95; // 95%

        // 배열값 하나가 들어올 경우 여기서 0이 됨. -> smallListFunction 에서 out of index 발생 -> 핸들링 코드 필요. -> 현재 배열의 인자가 하나일 경우 그 하나를 리턴. -> 엑셀 술식대로 라면 해당 데이터 자체가 리턴 돼야함.
        int first = excelUtils.intFunction(five);
        int second = excelUtils.intFunction(twentyFive);
        int third = excelUtils.intFunction(fifty);
        int fourth = excelUtils.intFunction(seventyFive);
        int fifth = excelUtils.intFunction(ninetyFive);

        int[] percentile1 = new int[] {first+1};
        int[] percentile2 = new int[] {second+1};
        int[] percentile3 = new int[] {third};
        int[] percentile3_1 = new int[] {third+1};
        int[] percentile4 = new int[] {fourth+1};
        int[] percentile5 = new int[] {fifth+1};

        ArrayList<Double> list1 = excelUtils.smallListFunction(diabetesList,percentile3);
        ArrayList<Double> list2 = excelUtils.smallListFunction(diabetesList,percentile3_1);

        double fiftyRound = excelUtils.roundFunction((list1.get(0)+list2.get(0))/2.0 , 0); // 나눗셈시 분자 분모가 둘다 정수면 정수 나누기가 되므로 소숫점은 자동 내림됨.

        map.put("5%", excelUtils.smallListFunction(diabetesList, percentile1));
        map.put("25%", excelUtils.smallListFunction(diabetesList, percentile2));
        map.put("50%", excelUtils.returnListRoundFunction(fiftyRound, 0));
        map.put("75%", excelUtils.smallListFunction(diabetesList, percentile4));
        map.put("95%", excelUtils.smallListFunction(diabetesList, percentile5));
        return ResponseEntity.ok().body(map);
    }


    @GetMapping("/diabetes-list/{id}")
    @ApiOperation(value = "일일 로그")
    @Timer
    public ResponseEntity<ArrayList<Double>> getDiabetesList ( // 일일 로그
            @PathVariable Long id,
            @RequestParam(value = "day", required = false) Integer day,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        UserInfoResDto userInfo = userService.findByIdx(id);
        List<CheckDeviceResponse> deviceList = userInfo.getCheckDevices();
        ArrayList<Double> diabetesList = new ArrayList<>();
        for(CheckDeviceResponse list : deviceList) {
            int monthValue = list.getCreateDataTime().getMonthValue();
            int dayOfMonth = list.getCreateDataTime().getDayOfMonth(); // 해당 날짜 객체의 일(DAY_OF_MONTH) 필드의 값을 반환함. (1~31) -> 날자만 가져오므로 month를 먼저 가져올것.
            if (month == null) {
                diabetesList.add(list.getDiabetesLevel()); // 전체 데이터
            } else if (month == monthValue) {
                if (day == null) { // 해당 달의 데이터
                    diabetesList.add(list.getDiabetesLevel());
                }else if(day == dayOfMonth){
                    diabetesList.add(list.getDiabetesLevel()); // 해당 일자의 데이터
                }
            }
        }
        return ResponseEntity.ok().body(diabetesList);
    }

    @GetMapping("/diabetes-list/start-end/{id}")
    @ApiOperation(value = "Start ~ End 로그")
    @Timer
    public ResponseEntity<List<DiabetesDto>> diabetesList ( // 일일 로그 -> 하루치씩 배열에 담아서. -> 반환값 dto
            @PathVariable Long id,
            @RequestParam(value = "startDay", required = false) String startDay, // 01
            @RequestParam(value = "endDay", required = false) String endDay // 14
    ) { // start ~ end 사이의 리스트 데이터 담을것. + 시간
        List<DiabetesDto> resultList = new ArrayList<>();
        List<Device> deviceList = deviceService.diabetesList(startDay, endDay, id);
        for(Device device : deviceList) {
            resultList.add(new DiabetesDto(device));
        }
        return ResponseEntity.ok().body(resultList);
    }


    /**
     * @Param : 유저 id
     * */
    @GetMapping("/report/blood/sugar/{id}") // 유저 id -> 보안 체크포인트 -> id가 숫자일 경우 숫자만 변경하면 타인의 정보를 열람할 수 있다.
    @ApiOperation(value = "14일치 리스트 데이터")
    public ResponseEntity<List<BloodSugarReportDto>> bloodSugarReport (
            @PathVariable Long id
    ) { // 14일치 혈당 값 리스트 -> 날짜
        List<BloodSugarReportDto> bloodSugarReports = new ArrayList<>();
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        for (int i=0; i < 10; i++) { //
            OptionalDouble average = deviceService.average(id);
            Double activatePercent = deviceService.activatePercent(id);
            LocalDateTime end = start.minusDays(1);
            start = end.minusDays(14);
            BloodSugarReportDto report = BloodSugarReportDto
                    .builder()
                    .startDay(start)
                    .endDay(end)
                    .activatePercent(activatePercent)
                    .average(average)
//                    .eventLowerCount() // 구현 예정
                    .build();
            bloodSugarReports.add(report);

        }
        return ResponseEntity.ok(bloodSugarReports);
    }


//    @GetMapping("/average/{id}")
//    @Timer
//    public ResponseEntity<?> diabetesAverage(
//            @PathVariable Long id){
//
//        UserInfoResDto userInfo = userService.findByIdx(id);
//        List<Long> diabetesLevelList = userInfo.getCheckDevices().stream().map(CheckDeviceResponse::getDiabetesLevel).collect(Collectors.toList()); // -> 모든 수치 다 나옴.
//
//        List<CheckDeviceResponse> deviceResponseList = userInfo.getCheckDevices();
//
//        Stream<LocalDateTime> time1 = deviceResponseList.stream().filter(s -> 70 <= s.getDiabetesLevel() && s.getDiabetesLevel() <= 180).map(CheckDeviceResponse::getCreateDataTime);
//        int sumMinutes1 = time1.mapToInt(LocalDateTime::getMinute).sum();
//
//        Stream<LocalDateTime> time2 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() < 70).map(CheckDeviceResponse::getCreateDataTime);
//        int sumMinutes2 = time2.mapToInt(LocalDateTime::getMinute).sum();
//
//        Stream<LocalDateTime> time3 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() < 54).map(CheckDeviceResponse::getCreateDataTime);
//        int sumMinutes3 = time3.mapToInt(LocalDateTime::getMinute).sum();
//
//        Stream<LocalDateTime> time4 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() > 180).map(CheckDeviceResponse::getCreateDataTime);
//        int sumMinutes4 = time4.mapToInt(LocalDateTime::getMinute).sum();
//
//        Stream<LocalDateTime> time5 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() > 250).map(CheckDeviceResponse::getCreateDataTime);
//        int sumMinutes5 = time5.mapToInt(LocalDateTime::getMinute).sum();
//
//
//        Stream<Long> level1 = diabetesLevelList.stream().filter(s ->  70 <= s && s <= 180); // 70 ~ 180
//        Stream<Long> level2 = diabetesLevelList.stream().filter(s -> s < 70); // 70 미만
//        Stream<Long> level3 = diabetesLevelList.stream().filter(s -> s < 54); // 54 미만
//        Stream<Long> level4 = diabetesLevelList.stream().filter(s -> s > 180); // 180 초과
//        Stream<Long> level5 = diabetesLevelList.stream().filter(s -> s > 250); // 250 초과
//
//        int level1Size = (int) level1.count();
//        double level1Percent = (double) level1Size/diabetesLevelList.size() * 100;
//
//        int level2Size = (int) level2.count();
//        double level2Percent = (double) level2Size/diabetesLevelList.size() * 100;
//
//        int level3Size = (int) level3.count();
//        double level3Percent = (double) level3Size/diabetesLevelList.size() * 100;
//
//        int level4Size = (int) level4.count();
//        double level4Percent = (double) level4Size/diabetesLevelList.size() * 100;
//
//        int level5Size = (int) level5.count();
//        double level5Percent = (double) level5Size/diabetesLevelList.size() * 100;
//
//        Map<String, Integer> map = new HashMap<>();
//        map.put("퍼센트1",(int) level1Percent);
//        map.put("시간1", sumMinutes1);
//
//        map.put("퍼센트2", (int) level2Percent);
//        map.put("시간2", sumMinutes2);
//
//        map.put("퍼센트3", (int) level3Percent);
//        map.put("시간3", sumMinutes3);
//
//        map.put("퍼센트4", (int) level4Percent);
//        map.put("시간4", sumMinutes4);
//
//        map.put("퍼센트5", (int) level5Percent);
//        map.put("시간5", sumMinutes5);
//
//
//        log.info("1번 사이즈 : {}", level1Size);
//        log.info("2번 사이즈 : {}", level2Size);
//        log.info("3번 사이즈 : {}", level3Size);
//        log.info("4번 사이즈 : {}", level4Size);
//        log.info("5번 사이즈 : {}", level5Size);
//
//        return ResponseEntity.ok(map);
//    }

    @GetMapping("/agp/{id}")
    @Timer
    public ResponseEntity<?> percentAndTime(
            @PathVariable Long id,
            @RequestParam List<Integer> range) {
        // 조건에 맞춰서 퍼센트 값 + 시간의 합 보내줘야함. -> 조건에 대한 데이터는 한번에 다 내려줘야함 (5개의 조건이 있으면 5번 호출 x 1번 호출 시 5개의 데이터 response)
        UserInfoResDto userInfo = userService.findByIdx(id);
        List<Double> diabetesLevelList = userInfo.getCheckDevices().stream().map(CheckDeviceResponse::getDiabetesLevel).collect(Collectors.toList()); // -> 모든 수치 다 나옴.
        List<CheckDeviceResponse> deviceResponseList = userInfo.getCheckDevices();

        Stream<LocalDateTime> time1 = deviceResponseList.stream().filter(s -> range.get(0) <= s.getDiabetesLevel() && s.getDiabetesLevel() <= range.get(1)).map(CheckDeviceResponse::getCreateDataTime);
        int minutes1 = (int) (time1.count() * 2); // -> 2분씩 더해지는게 아니라 2 + 4 + 6 + 이런식으로 되므로 정확한 시간이 안나온다. 갯수 x 2 로 하는게 맞다.
        Stream<LocalDateTime> time2 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() < range.get(2)).map(CheckDeviceResponse::getCreateDataTime);
        int minutes2 = (int) (time2.count() * 2);
        Stream<LocalDateTime> time3 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() < range.get(3)).map(CheckDeviceResponse::getCreateDataTime);
        int minutes3 = (int) (time3.count() * 2);
        Stream<LocalDateTime> time4 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() > range.get(4)).map(CheckDeviceResponse::getCreateDataTime);
        int minutes4 = (int) (time4.count() * 2);
        Stream<LocalDateTime> time5 = deviceResponseList.stream().filter(s -> s.getDiabetesLevel() > range.get(5)).map(CheckDeviceResponse::getCreateDataTime);
        int minutes5 = (int) (time5.count() * 2);

        Stream<Double> level1 = diabetesLevelList.stream().filter(s ->  range.get(0) <= s && s <= range.get(1)); // 70 ~ 180
        Stream<Double> level2 = diabetesLevelList.stream().filter(s -> s < range.get(2)); // 70 미만
        Stream<Double> level3 = diabetesLevelList.stream().filter(s -> s < range.get(3)); // 54 미만
        Stream<Double> level4 = diabetesLevelList.stream().filter(s -> s > range.get(4)); // 180 초과
        Stream<Double> level5 = diabetesLevelList.stream().filter(s -> s > range.get(5)); // 250 초과

        int size1 = (int) level1.count();
        double level1Percent = (double) size1/diabetesLevelList.size() * 100;
        int size2 = (int) level2.count();
        double level2Percent = (double) size2/diabetesLevelList.size() * 100;
        int size3 = (int) level3.count();
        double level3Percent = (double) size3/diabetesLevelList.size() * 100;
        int size4 = (int) level4.count();
        double level4Percent = (double) size4/diabetesLevelList.size() * 100;
        int size5 = (int) level5.count();
        double level5Percent = (double) size5/diabetesLevelList.size() * 100;

        Map<String, Integer> map = new HashMap<>();
        map.put("퍼센트1", (int) level1Percent);
        map.put("시간1",  minutes1);
        map.put("퍼센트2", (int) level2Percent);
        map.put("시간2", minutes2);
        map.put("퍼센트3", (int) level3Percent);
        map.put("시간3", minutes3);
        map.put("퍼센트4", (int) level4Percent);
        map.put("시간4", minutes4);
        map.put("퍼센트5", (int) level5Percent);
        map.put("시간5", minutes5);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/diabetes/average/{id}")
    @ApiOperation(value = "평균")
    public ResponseEntity<OptionalDouble> average (
            @PathVariable Long id
    ) {
        UserInfoResDto userInfo = userService.findByIdx(id);
        OptionalDouble average = userInfo.getCheckDevices().stream().map(CheckDeviceResponse::getDiabetesLevel).mapToDouble(Double::doubleValue).average();
        return ResponseEntity.ok(average);
    }

    // 활성화 % ->  들어온 데이터 갯수 / 들어와야 된 데이터 갯수 -> 2분당 1개 -> 1시간당 30개 -> 24시간 720개 -> 14일 기준 10080
    @GetMapping("/activate/percent/{id}")
    @ApiOperation(value = "활성화 퍼센트")
    public ResponseEntity<?> activate ( // 일자 기준을 파라미터로 입력을 받을지? 아니면 14일 고정을 할지?
            @PathVariable Long id,
            @RequestParam(required = false) Integer day
    ) {
        UserInfoResDto userInfo = userService.findByIdx(id);
        int size = (int) userInfo.getCheckDevices().stream().map(CheckDeviceResponse::getDiabetesLevel).count(); // 유저의 전체 데이터
        double activatePercent = 0;
        if (day != null) {
            int denominator = day * 720;
            activatePercent = (size / denominator) * 100;

        } else {
            activatePercent = (size/10080) * 100;
        }
        return ResponseEntity.ok(activatePercent);
    }



}
