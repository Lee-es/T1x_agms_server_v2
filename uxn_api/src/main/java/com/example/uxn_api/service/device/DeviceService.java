package com.example.uxn_api.service.device;

import com.example.uxn_api.web.device.dto.req.DeviceRegistrationDto;
import com.example.uxn_api.web.device.dto.res.*;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.h2.util.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;



    @Transactional
    public Long registration(List<DeviceRegistrationDto> dto){
//    public List<DeviceRegistrationDto> registration(List<DeviceRegistrationDto> dto){

        if(dto.size()>0){
            List<Device> saveData=new ArrayList<>();

            for(DeviceRegistrationDto data :dto){
                saveData.add(data.toEntity());
            }

            try {
                log.debug("registration saveAll start");

                deviceRepository.saveAll(saveData);

                log.debug("registration saveAll finish");
            }catch (Exception e){
                if(saveData != null)
                    saveData=null;

                log.debug("registration Exception :" + e);
                return 500L;
            }

            if(saveData != null) {
                saveData=null;
                log.debug("registration - set saveData null");
            }

        }//if

//        return dto;
        return 200L;
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> diabetesInfo(Long userId){
        User user = userRepository.findByIdx(userId);
        List<Device> deviceList = deviceRepository.findByUser(user);
        return deviceList.stream().map(UserInfoResponse::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Device> diabetesList(String startDay, String endDay, Long idx) {
        User user = userRepository.findByIdx(idx);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return deviceRepository.findByCreateDataTimeBetweenAndUser(LocalDateTime.parse(startDay,formatter), LocalDateTime.parse(endDay,formatter), user);
    }

//    @Transactional(readOnly = true)
//    public List<DiabetesListDto> dayList(String day) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        List<Device> deviceList = deviceRepository.findByCreateDataTime(LocalDateTime.parse(day, formatter));
//    }

//    @Transactional(readOnly = true)
//    public List<DiabetesDto> weekList(String date) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    }

    @Transactional(readOnly = true)
    public OptionalDouble average(Long id) { // 평균
        User user = userRepository.findByIdx(id);
        return user.getDevices().stream().map(Device::getDiabetesLevel).mapToDouble(Double::doubleValue).average();
    }

    @Transactional
    public Double activatePercent(Long id) { // 14일치 고정
        User user = userRepository.findByIdx(id);
        Long size = user.getDevices().stream().map(Device::getDiabetesLevel).count();
        double activatePercent = 0;
        activatePercent = (size/10080) * 100;

        return activatePercent;

    }


    @Transactional
    public WeakHashMap<String, Object> getLastTimeData(Long id){
        User user = userRepository.findByIdx(id);
      //  List<String> deviceList = deviceRepository.checkData(user);
        Device deviceLastTime = deviceRepository.findTopByUserOrderByCreateDataTimeDesc(user);
        WeakHashMap<String, Object> data = new WeakHashMap<>();
        if(deviceLastTime!=null){
            data.put("success",true);
            String datetime=deviceLastTime.getCreateDataTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            data.put("date",datetime);
        }else{
            data.put("success",false);
            data.put("date","");
        }

        return data;
    }



}
