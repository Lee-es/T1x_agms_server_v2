package com.example.uxn_api.service.report;

import com.example.uxn_api.web.calibration.dto.res.CalibrationListResDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_api.web.note.dto.res.NoteListResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_api.web.report.dto.req.*;
import com.example.uxn_api.web.report.dto.res.*;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


import com.example.uxn_api.service.user.UserService;

import com.example.uxn_common.global.utils.excel.ExcelUtils;

import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    private final NoteRepository noteRepository;

    private final CalibrationRepository calibrationRepository;

    private static final int DATE_RANGE = 2880;//60분 x 24시간 x 2일

    private final double toVeryLow = 1.3;
    private final double toVeryHigh = 1.39;
    
    public ReportItemWrapper getReportItemList(Long userIdx, int page){
        ReportItemWrapper wrapper = new ReportItemWrapper();

        List<ReportItemDto> result = new ArrayList<>();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        User user = userRepository.findByIdx(userIdx);
        double lowerValue = user.getMinGlucose();
        double veryLower = lowerValue / toVeryLow;
        double higherValue = user.getMaxGlucose();
        double veryHigher = higherValue * toVeryHigh;
        List<String> groupByDateList = deviceRepository.customGetDateList(user);

        //start of safe code
//        List<String> groupByDateList = new ArrayList<>();
//        HashMap<String,String> tmpDateMap = new HashMap<>();
//        List<Device> allDataByUser = deviceRepository.findByUser(user);
//        for(Device tmpDevice : allDataByUser){
//            String tmpKey = tmpDevice.getCreateDataTime().format(dateFormat);
//
//            tmpDateMap.put(tmpKey, tmpKey);
//        }
//        if(!tmpDateMap.isEmpty()){
//            for(String key : tmpDateMap.keySet()){
//
//                groupByDateList.add(key);
//            }
//        }
        //end of safe code

        List<LocalDateTime[]> dateList = new ArrayList<>();
        if(groupByDateList!=null && groupByDateList.size()>0 ){
            log.info("groupByDateList:"+groupByDateList.size());
            LocalDateTime lastDate = null;
            LocalDateTime startDate = null;
            for(String data : groupByDateList){
                LocalDateTime current = null;
                try {
                    current = LocalDateTime.parse(data, dateFormat);
                } catch (Exception e) {
                    // TODO: handle exception
                    try {
                        String[] tmpArray = data.split("-");
                        current = LocalDateTime.of(Integer.valueOf(tmpArray[0]),Integer.valueOf(tmpArray[1]),
                        Integer.valueOf(tmpArray[2]), 23, 59, 59);
                    } catch (Exception e2) {
                        current = LocalDateTime.now();
                    }
                    
                }
                
                if(lastDate == null){
                    lastDate = current;
                    //3일치만 가져오기(ex:11/27/0시~11/29/마지막 시간) - 2023.02.06, ykw
                    startDate = current.minusMinutes(DATE_RANGE).plusSeconds(1);
                    LocalDateTime[] tmpArray = new LocalDateTime[2];
                    tmpArray[0] = startDate;
                    tmpArray[1] = lastDate;
                    dateList.add(tmpArray);
                } else {
                    if(!startDate.isBefore(current)){
                        lastDate = current;
                        //3일치만 가져오기(ex:11/27/0시~11/29/마지막 시간) - 2023.02.06, ykw
                        startDate = current.minusMinutes(DATE_RANGE).plusSeconds(1);
                        LocalDateTime[] tmpArray = new LocalDateTime[2];
                        tmpArray[0] = startDate;
                        tmpArray[1] = lastDate;
                        dateList.add(tmpArray);
                    } 
                }
            }
        }
        if(dateList == null || dateList.isEmpty()){
            log.error("dateList == null || dateList.isEmpty()");
            wrapper.setList(result);
            return wrapper;
        }
        int totalPage = 0;

        if(dateList!=null && !dateList.isEmpty()){
            double tmpPage = dateList.size() / 20.0;
            log.info("tmpPage:"+tmpPage);
            totalPage = (int) Math.ceil(tmpPage);
            log.info("after ceil:"+totalPage);
            try{
                int lastIndex = (page*20);
                if(lastIndex>dateList.size()){
                    lastIndex = dateList.size();
                }
                log.info("dateList:"+dateList.size());
                log.info("firstIndex:"+(page-1)*20);
                log.info("lastIndex:"+lastIndex);
                dateList = dateList.subList((page-1)*20, lastIndex);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        for(LocalDateTime[] timeArray : dateList){

            LocalDateTime end = timeArray[1];
            LocalDateTime start  = timeArray[0];
            log.info("start, end : {},{}", start,end);

            ReportItemDto dto = new ReportItemDto();

            double averageGlucose = 0;
            double minGlucose = 0;
            double maxGlucose = 0;
            String deviceID="";

            long lowGlucoseCount = 0;
            long veryLowGlucoseCount = 0;
            long lowerGlucoseCount =0;// lowGlucoseCount + veryLowGlucoseCount;
            long targetGlucoseCount = 0;
            long highGlucoseCount = 0;
            long veryHighGlucoseCount = 0;

            long dayRange = start.until(end.plusDays(1), ChronoUnit.DAYS );//edit - 2023.02.06, ykw
            long minuteRange = start.until(end.plusMinutes(1), ChronoUnit.MINUTES );;//edit - 2023.02.06, ykw

            long dataCount = 0;

            float activatePercent =0;
            
            List<Device> list = deviceRepository.findByCreateDataTimeBetweenAndUser(start, end, user);
//            double sum = 0;
//            for(Device device : list){
//                //compare glucose
//                try {
//                    if(device.getDiabetesLevel() > veryHigher){
//                        veryHighGlucoseCount++;
//                    } else if(device.getDiabetesLevel() > higherValue){
//                        highGlucoseCount++;
//                    } else if(device.getDiabetesLevel()>lowerValue){
//                        targetGlucoseCount++;
//                    } else if(device.getDiabetesLevel()>veryLower){
//                        lowGlucoseCount++;
//                    } else{
//                        veryLowGlucoseCount++;
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
////                sum += device.getDiabetesLevel();
//            }

            int size = list.size() - 1;
            deviceID = user.getDevices().stream().map(Device::getDeviceId).collect(Collectors.toList()).get((int) (size));


            //데이터 일 수
            dataCount = list.size();
            try{
                activatePercent =  Math.round(
                        (float)(dataCount / (float)minuteRange * 1000.0f)
                )/10.0f;
            }catch (Exception e){
                activatePercent =0 ;
                e.printStackTrace();
            }

            //average, min, max Glucose
            //add get min, max - 2023.02.06, ykw
            try{
//                averageGlucose = Math.round(
//                        sum / (double)dataCount*100.0f
//                )/100.0;

                List<Double> diabetesList;
                diabetesList = list.stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
                OptionalDouble avg = diabetesList.stream().mapToDouble(Double::doubleValue).average();
                OptionalDouble min = diabetesList.stream().mapToDouble(Double::doubleValue).min();
                OptionalDouble max = diabetesList.stream().mapToDouble(Double::doubleValue).max();

                averageGlucose = (double)Math.round(avg.getAsDouble() * 100) / 100.0;
                minGlucose = (double)Math.round(min.getAsDouble() * 100) / 100.0;
                maxGlucose = (double)Math.round(max.getAsDouble() * 100) / 100.0;
            }catch (Exception e){
                e.printStackTrace();
            }


//            log.info("sum:"+sum);
            log.info("dataCount:"+dataCount);
            log.info("averageGlucose:"+averageGlucose);


            lowerGlucoseCount = lowGlucoseCount + veryLowGlucoseCount;

            dto.setAverageGlucose(averageGlucose);
            dto.setAverageGlucoseString(String.valueOf(averageGlucose));
            dto.setMinGlucose(minGlucose);
            dto.setMaxGlucose(maxGlucose);
            dto.setDataCount(dataCount);
            dto.setDeviceID(deviceID);
            dto.setDataMinute(dataCount);
            dto.setEndDateTime(end);
            dto.setEndDate(end.toLocalDate());
            dto.setLowerGlucoseCount(lowerGlucoseCount);
            dto.setProfileGraphData(createTmpList(user, start,end));
            dto.setSensorActivePercent(activatePercent);
            dto.setSensorActivePercentString(String.valueOf(activatePercent));
            dto.setStartDate(start.toLocalDate());
            dto.setStartDateTime(start);
            dto.setWholeDataCount(minuteRange);
            dto.setWholeDataMinute(minuteRange);
            result.add(dto);
        }
        wrapper.setList(result);
        wrapper.setLastPage(totalPage);
        return wrapper;
    }
    private HashMap<String, ArrayList> createTmpList(User user,LocalDateTime start, LocalDateTime end){
        HashMap<String, ArrayList> result = new HashMap<>();
        
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<String> list5 = new ArrayList<>();
        ArrayList<String> list25 = new ArrayList<>();
        ArrayList<String> list50 = new ArrayList<>();
        ArrayList<String> list75 = new ArrayList<>();
        ArrayList<String> list95 = new ArrayList<>();

        //측정 기간 데이터 가져오기
        List<Device> list = deviceRepository.findByCreateDataTimeBetweenAndUser(start, end, user);

        //통계를 위한 데이터 시작 가져오기
        // 시작시간을 기준으로 10초 or 1분 단위로 데이터 가져오기 -> 시간 간격이 달라질 수 있다...
//        LocalTime startTime = LocalTime.of(0, 0, 0);
//        LocalTime endTime = LocalTime.of(23, 59, 0);
        LocalTime startTime = list.get(0).getCreateDataTime().toLocalTime();
        LocalTime current = startTime;


        HashMap<String, List<Double>> dataMap = new HashMap<>();
        for(Device device : list){
            String key = device.getCreateDataTime().format(timeFormat);
            List<Double> inner = dataMap.get(key);
            if(inner == null) {
                inner = new ArrayList<>();
            }
            inner.add(device.getDiabetesLevel());
            dataMap.put(key, inner);
        }

        for(int i=0;i<24*60;i++) {
            String key = current.format(timeFormat);
            List<Double> diabetesList = dataMap.get(key);
            if(diabetesList!=null){

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

                double fiftyRound = excelUtils.roundFunction((list1.get(0)+list2.get(0))/2.0 , 0); // 나눗셈시 분자 분모가 둘다 정수면 정수 나누기가 되므

                dateList.add(current.format(timeFormat));
                
                list5.add(String.valueOf(excelUtils.smallListFunction(diabetesList, percentile1).get(0)));
                list25.add(String.valueOf(excelUtils.smallListFunction(diabetesList, percentile2).get(0)));
                list50.add(String.valueOf(excelUtils.returnListRoundFunction(fiftyRound, 0).get(0)));
                list75.add(String.valueOf(excelUtils.smallListFunction(diabetesList, percentile4).get(0)));
                list95.add(String.valueOf(excelUtils.smallListFunction(diabetesList, percentile5).get(0)));
            }

            //다음 데이터
//            current = current.plusMinutes(1);//측정 간격이 일정하지 않기 때문에 수정이 필요하다...23/02.16, ykw
            current = current.plusSeconds(10);//시작 시간 기준으로 10초 간격 - 10초 간격이 아닐수도 있다...예외 처리필요 ykw
        }

        result.put("date", dateList);
        result.put("5%", list5);
        result.put("25%", list25);
        result.put("50%", list50);
        result.put("75%", list75);
        result.put("95%", list95);

        return result;
    }

    public ReportDetailDto getReport(Long userIdx, LocalDate startDate, LocalDate endDate){
        User user = userRepository.findByIdx(userIdx);
        double lowerValue = user.getMinGlucose();
        double veryLower = lowerValue / toVeryLow;
        double higherValue = user.getMaxGlucose();
        double veryHigher = higherValue * toVeryHigh;

        ReportDetailDto dto = new ReportDetailDto();
        double averageGlucose = 0;
            
        long lowGlucoseCount = 0;
        long veryLowGlucoseCount = 0;

        long highGlucoseCount = 0;
        long veryHighGlucoseCount = 0;

        long lowerGlucoseCount = 0;
        long higherGlucoseCount = 0;
        long targetGlucoseCount = 0;

        long dayRange = startDate.until(endDate.plusDays(1), ChronoUnit.DAYS );//3일 -> 2일 - 2023.02.09, ykw
        dto.setStartDateTime(LocalDateTime.of(endDate, LocalTime.of(0, 0, 0)));
        dto.setEndDateTime(LocalDateTime.of(endDate, LocalTime.of(23, 59, 59)));

        long minuteRange = dto.getStartDateTime().until(dto.getEndDateTime(), ChronoUnit.MINUTES );
        
        long dataCount = (long) (minuteRange * 0.9);



        float activatePercent = 0;

        List<Device> list = deviceRepository.findByCreateDataTimeBetweenAndUser(dto.getStartDateTime(),dto.getEndDateTime(), user);
//        double sum = 0;
//        int hour =0;
//        int index  = 0;
//        boolean insert = false;
//        for(Device device : list){
//            //compare glucose
//
//            insert = false;
//            try {
//                if(device.getDiabetesLevel() > veryHigher){
//                    veryHighGlucoseCount++;
//                    insert = true;
//                } else if(device.getDiabetesLevel() > higherValue){
//                    highGlucoseCount++;
//                    insert = true;
//                } else if(device.getDiabetesLevel()>lowerValue){
//                    targetGlucoseCount++;
//                } else if(device.getDiabetesLevel()>veryLower){
//                    lowGlucoseCount++;
//                    insert = true;
//                } else{
//                    veryLowGlucoseCount++;
//                    insert = true;
//
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            sum += device.getDiabetesLevel();
//
//        }

        try{
            activatePercent =  Math.round(
                    (float)(dataCount / (float)minuteRange * 1000.0f)
            )/10.0f;
        }catch (Exception e){
            activatePercent =0 ;
            e.printStackTrace();
        }
        try{
//            averageGlucose = Math.round(
//                    sum / (double)dataCount*100.0f
//            )/100.0;
            List<Double> diabetesList;
            diabetesList = list.stream().map(Device::getDiabetesLevel).collect(Collectors.toList());
            OptionalDouble avg = diabetesList.stream().mapToDouble(Double::doubleValue).average();

            averageGlucose = (double)Math.round(avg.getAsDouble() * 100) / 100.0;

        }catch (Exception e){
            e.printStackTrace();
        }
        dto.setAverageGlucose((float) averageGlucose);
        dto.setAverageGlucoseString(String.valueOf(averageGlucose));
        dto.setDataCount(dataCount);
        dto.setDataMinute(dataCount);
        
        dto.setEndDate(endDate);
        dto.setLowerGlucoseCount(lowerGlucoseCount);
        
        dto.setSensorActivePercent(activatePercent);
        dto.setSensorActivePercentString(String.valueOf(activatePercent));
        dto.setStartDate(startDate);
        
        dto.setProfileGraphData(createTmpList(user, dto.getStartDateTime(),dto.getEndDateTime()));

        dto.setWholeDataCount(minuteRange);
        dto.setWholeDataMinute(minuteRange);
        dto.setDateRange((int) dayRange);
        
        dto.setDayGraphDataList(createDayGraphDto(user, startDate,endDate.plusDays(1)));
        ArrayList<DeviceInformation> deviceInfo = new ArrayList<>();
        deviceInfo.add(DeviceInformation.builder()
            .appVersion("1.0")
            .firmware("1.0.0")
            .maxStandard(user.getMaxGlucose())
            .minimumStandard(user.getMinGlucose())
            .osName("Android")
            .osName("12")
            .smartPhone("Samsung")
            .build());
        dto.setDeviceInformations(
            deviceInfo
        );
        dto.setGlucoseCV(12);
        dto.setGmi(13);
        dto.setGmiMol(33);
        dto.setHighGlucoseCount(highGlucoseCount);
        dto.setHighGlucoseMinute(highGlucoseCount);
        dto.setHighGlucosePercent( (float)highGlucoseCount / (float) dataCount * 100.0f);

        dto.setLowGlucoseCount(lowGlucoseCount);
        dto.setLowGlucoseMinute(lowGlucoseCount);
        dto.setLowGlucosePercent(lowGlucoseCount / (float)dataCount   * 100.0f);

        dto.setImportantPattern(createImportantPattern(startDate, endDate));

        dto.setSensorActivePercent(activatePercent);
        dto.setSensorActivePercentString(String.valueOf(activatePercent));

        dto.setProfileGraphData(createTmpList(user,dto.getStartDateTime(),dto.getEndDateTime()));
        dto.setTargetGlucoseCount(targetGlucoseCount);
        dto.setTargetGlucoseMinute(targetGlucoseCount);
        dto.setTargetGlucosePercent(targetGlucoseCount / (float)dataCount * 100.0f);

        dto.setVeryHighGlucoseCount(veryHighGlucoseCount);
        dto.setVeryHighGlucoseMinute(veryHighGlucoseCount);
        dto.setVeryHighGlucosePercent(veryHighGlucoseCount / (float)dataCount * 100.0f);
        dto.setVeryLowGlucoseCount(veryLowGlucoseCount);
        dto.setVeryLowGlucoseMinute(veryLowGlucoseCount);
        dto.setVeryLowGlucosePercent(veryLowGlucoseCount / (float)dataCount * 100.0f);


        return dto;
    }
    private ImportantPattern createImportantPattern(LocalDate startDate, LocalDate endDate){
        ArrayList<String> question = new ArrayList<>();
        question.add("질문");
        ImportantPattern importantPattern =  ImportantPattern.builder()
        .kindKeyword("오전")
        .lifeStyle("음식")
        .lifeStyleQuestion(question)
        .build();
        

        return importantPattern;
    }
    private ArrayList<DayGraphDto> createDayGraphDto(User user,LocalDate startDate, LocalDate endDate){
        long dayRange = startDate.until(endDate, ChronoUnit.DAYS );
        double lowerValue = user.getMinGlucose();
        double veryLower = lowerValue / toVeryLow;
        double higherValue = user.getMaxGlucose();
        double veryHigher = higherValue * toVeryHigh;
        LocalDate now = startDate;
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        ArrayList<DayGraphDto> list = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        int index = 0;
        int hour = 0;
//        boolean insert = false;
        for(int i=0;i<dayRange;i++){
            
            List<Device> deviceList = deviceRepository.findByCreateDataTimeBetweenAndUser(
                LocalDateTime.of(now,
                    LocalTime.of(0, 0, 0)
                ),
                LocalDateTime.of(now,
                    LocalTime.of(23, 59, 59)
                ), user);

            DayGraphDto dto = new DayGraphDto();
            ArrayList<Double> dataList = new ArrayList<>();
            ArrayList<LocalDateTime> timeList = new ArrayList<>();


            dto.setSummary(DaySummary.builder()
            .averageGlucose(13)
            .lowerGlucoseCount(2)
            .scanCount(deviceList.size())
            .targetDate(now)
            .build());
            dto.setTargetDate(now);

            //Note와 Calibration값을 1일치씩 가져오도록 수정 - 2023.03.17
//            List<Note> noteList = noteRepository.findByUserAndCreateDateBetween(user,LocalDateTime.of(startDate,LocalTime.of(0,0)),
//                    LocalDateTime.of(endDate,LocalTime.of(23,59,59,999)));
//
//            List<Calibration> calibrationList =
//                    calibrationRepository.findByUserAndCreatedDateBetween(user,LocalDateTime.of(startDate,LocalTime.of(0,0)),
//                            LocalDateTime.of(endDate,LocalTime.of(23,59,59,999)));

            List<Note> noteList = noteRepository.findByUserAndCreateDateBetween(user,LocalDateTime.of(now,LocalTime.of(0,0)),
                    LocalDateTime.of(now,LocalTime.of(23,59,59,999)));

            List<Calibration> calibrationList =
                    calibrationRepository.findByUserAndCreatedDateBetween(user,LocalDateTime.of(now,LocalTime.of(0,0)),
                            LocalDateTime.of(now,LocalTime.of(23,59,59,999)));

            if(calibrationList!=null){
                for(Calibration calibration : calibrationList){
                    dto.getCalibrationList().add(new CalibrationListResDto(calibration));
                    hour = calibration.getCreatedDate().getHour();
                    if(hour <2){
                        index = 0;
                    } else if(hour <4){
                        index  = 1;
                    } else if(hour <6){
                        index  = 2;
                    } else if(hour <8){
                        index  = 3;
                    } else if(hour <10){
                        index  = 4;
                    } else if(hour <12){
                        index  = 5;
                    } else if(hour <14){
                        index  = 6;
                    } else if(hour <16){
                        index  = 7;
                    } else if(hour <18){
                        index  = 8;
                    } else if(hour <20){
                        index  = 9;
                    } else if(hour <22){
                        index  = 10;
                    } else {
                        index  = 11;
                    }
                    ArrayList<String> tmp = dto.getValueList()[index];
                    if(tmp ==null){
                        tmp = new ArrayList<>();
                    }
                    dto.getValueList()[index] = tmp;
                    if(tmp.size()<4){
                        tmp.add(calibration.getContents());
                    }
                }
            }
            if(noteList!=null){
                int count = 0;
                HashMap<Integer, Integer> noteCounter = new HashMap<>();
                for(Note note : noteList){
                    dto.getNoteList().add(new NoteListResDto(note));
                    hour = note.getCreateDate().getHour();
                    if(hour <2){
                        index = 0;
                    } else if(hour <4){
                        index  = 1;
                    } else if(hour <6){
                        index  = 2;
                    } else if(hour <8){
                        index  = 3;
                    } else if(hour <10){
                        index  = 4;
                    } else if(hour <12){
                        index  = 5;
                    } else if(hour <14){
                        index  = 6;
                    } else if(hour <16){
                        index  = 7;
                    } else if(hour <18){
                        index  = 8;
                    } else if(hour <20){
                        index  = 9;
                    } else if(hour <22){
                        index  = 10;
                    } else {
                        index  = 11;
                    }
                    Integer tmp = noteCounter.get(index);
                    int tmpValue = 0;
                    if(tmp!=null){
                        tmpValue = tmp.intValue();
                    }
                    tmpValue ++;
                    noteCounter.put(index,tmpValue);
                }
                if(!noteCounter.isEmpty()){
                    for(Integer key : noteCounter.keySet()){
                        ArrayList<String> tmp = dto.getValueList()[key.intValue()];
                        if(tmp ==null){
                            tmp = new ArrayList<>();
                        }
                        dto.getValueList()[key.intValue()] = tmp;
                        if(tmp.size()<4){
                            tmp.add("이벤트 "+ noteCounter.get(key).intValue()+ "개");
                        }
                    }
                }
            }
            
            for(Device device :deviceList){
                timeList.add(device.getCreateDataTime());//.format(timeFormat));
                dataList.add( device.getDiabetesLevel());


//                try {
//                    if(device.getDiabetesLevel() > veryHigher){
//                        insert = true;
//                    } else if(device.getDiabetesLevel() > higherValue){
//                        insert = true;
//                    } else if(device.getDiabetesLevel()>lowerValue){
//
//                    } else if(device.getDiabetesLevel()>veryLower){
//
//                        insert = true;
//                    } else{
//
//                        insert = true;
//
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

//                try {
//                    if(insert){
//                        hour = device.getCreateDataTime().getHour();
//                        if(hour <2){
//                            index = 0;
//                        } else if(hour <4){
//                            index  = 1;
//                        } else if(hour <6){
//                            index  = 2;
//                        } else if(hour <8){
//                            index  = 3;
//                        } else if(hour <10){
//                            index  = 4;
//                        } else if(hour <12){
//                            index  = 5;
//                        } else if(hour <14){
//                            index  = 6;
//                        } else if(hour <16){
//                            index  = 7;
//                        } else if(hour <18){
//                            index  = 8;
//                        } else if(hour <20){
//                            index  = 9;
//                        } else if(hour <22){
//                            index  = 10;
//                        } else {
//                            index  = 11;
//                        }
//                        ArrayList<String> tmp = dto.getValueList()[index];
//                        if(tmp ==null){
//                            tmp = new ArrayList<>();
//                        }
//                        dto.getValueList()[index] = tmp;
//                        if(tmp.size()<4){
//                            tmp.add(String.valueOf(Math.round(device.getDiabetesLevel()*100.0)/100.0));
//                        }
//                    }
//
//
//                }catch (Exception e){
//
//                }
            }

            dto.setDataList(dataList);
            dto.setTimeStringList(timeList);
            list.add(dto);
            now = now.plusDays(1);
        }
        return list;
    }

    public void csvFile(LocalDate start, LocalDate end, String userEmail, HttpServletResponse response) {
        User user = userRepository.findByEmail(userEmail);
        LocalDateTime startTime = LocalDateTime.of(start, LocalTime.of(0, 0, 0));
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.of(23,59,59));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Note> noteList = noteRepository.findByUserAndCreateDateBetween(user,startTime,endTime);
        List<Calibration> calibrationList = calibrationRepository.findByUserAndCreatedDateBetween(user,startTime,endTime);
        List<Device> list = deviceRepository.findByCreateDataTimeBetweenAndUser(startTime,endTime, user);
//        try {
//
//            response.getOutputStream().write("\uFEFF".getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try(PrintWriter writer = new PrintWriter(response.getOutputStream(), false)){
            CSVWriter csvWriter = new CSVWriter(writer);
            String[] entries = new String[10];
            entries[0] = "Device_Mac";
            entries[1] = "Time";
            entries[2] = "We_Current(nA)";
            entries[3] = "Ae_Current(nA)";
            entries[4] = "Battery_Level(V)";
            entries[5] = "Ref";
            entries[6] = "We_Electric_Potential";
            entries[7] = "Ae_Electric_Potential";
            entries[8] = "Event";
            entries[9] = "SBGM";
            csvWriter.writeNext(entries);
            String[] tmp = new String[10];

            //glucose
            for(Device device : list) {
             tmp[0] = null;
             tmp[1] = null;
             tmp[2] = null;
             tmp[3] = null;
             tmp[4] = null;
             tmp[5] = null;
             tmp[6] = null;
             tmp[7] = null;
             tmp[8] = null;
             tmp[9] = null;



//             tmp[0] = formatter.format(device.getCreateDataTime());
             tmp[0] = device.getDeviceId();
             tmp[1] = formatter.format(device.getCreateDataTime());
//             tmp[2] = device.getDiabetesLevel().toString();
//             Double diabetesLevel = Math.round(device.getDiabetesLevel() *100) / 100.0;
             tmp[2] = device.getDiabetesLevel().toString();
             tmp[3] = device.getAe_current()!=null?device.getAe_current().toString():null;
             tmp[4] = device.getBatteryLevel()!=null?device.getBatteryLevel().toString():null;
             tmp[5] = device.getRef()!=null?device.getRef().toString():null;
             tmp[6] = device.getWe_p()!=null?device.getWe_p().toString():null;
             tmp[7] = device.getAe_p()!=null?device.getAe_p().toString():null;
             writer.flush();
             csvWriter.writeNext(tmp);
//            log.info("시간 , 수치 : {}, {}", device.getCreateDataTime().toString(), device.getDiabetesLevel().toString());
            }
            //Event
            if(noteList!=null){
                for(Note note: noteList){
                    tmp[0] = null;
                    tmp[1] = null;
                    tmp[2] = null;
                    tmp[3] = null;
                    tmp[4] = null;
                    tmp[5] = null;
                    tmp[6] = null;
                    tmp[7] = null;
                    tmp[8] = null;
                    tmp[9] = null;


                    tmp[0] = "Event";
                    tmp[1] = note.getCreateDate()!=null?formatter.format(note.getCreateDate()):null;
                    tmp[8] = note.getContents();
                    writer.flush();
                    csvWriter.writeNext(tmp);
                }
            }
            if(calibrationList!=null){
                for(Calibration calibration: calibrationList){
                    tmp[0] = null;
                    tmp[1] = null;
                    tmp[2] = null;
                    tmp[0] = "SBGM";
                    tmp[1] = calibration.getCreatedDate()!=null?formatter.format(calibration.getCreatedDate()):null;
                    tmp[9] = calibration.getContents();
                    writer.flush();
                    csvWriter.writeNext(tmp);
                }
            }
            csvWriter.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
