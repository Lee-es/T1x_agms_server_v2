package com.example.uxn_api.web.device.controller;

import com.example.uxn_api.service.device.DeviceService;
import com.example.uxn_api.web.device.dto.req.DeviceRegistrationDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diabetes")
@Slf4j
public class DeviceApiController {

    private final DeviceService deviceService;
    private final UserRepository userRepository;


    @PostMapping("/save/diabetes-info")
    @ApiOperation(value = "실시간 당뇨 정보 등록", notes = "장치가 연결 돼 있으면 실시간을 당뇨 정보를 등록해 주고 / 꺼지면 이벤트 종료 -> 데이터 전송 x")
//    public ResponseEntity<List<DeviceRegistrationDto>> registration(@RequestBody List<DeviceRegistrationDto> dto, @RequestParam(required = false) boolean turnOn){
    public ResponseEntity<String> registration(@RequestBody List<DeviceRegistrationDto> dto, @RequestParam(required = false) boolean turnOn){
//        User user = userRepository.findById(dto.get(0).getUser_id()).orElseThrow();
//        if(user == null){
//            log.error("registration - user is null, getUser id : "+ dto.get(0).getUser_id());
//
//            return null;
//        }

        if (turnOn){
            log.info("Data 전송 이벤트 동작 : User id = " + dto.get(0).getUser_id());

//            user.eventCheckUpdate(true);
//            userRepository.save(user);

            int size = dto.size();
            log.debug("device registration save : size = "  + size);

            Long ret = deviceService.registration(dto);

            log.info("device registration finish");

            if(dto.isEmpty() == false) {
                dto.clear();
                log.debug("DeviceRegistrationDto clear");
            }

            return ResponseEntity.ok().body(ret.toString());
//            return ResponseEntity.ok().body(deviceService.registration(dto));
        }else {
            // todo :: 일정 시간 이후(2분?)로 데이터가 안들어오면 해당 로직 동작 하게 할지
            log.info("Data 전송 이벤트 종료 : User id = " + dto.get(0).getUser_id());
//            user.eventCheckUpdate(false);
//            userRepository.save(user);
            return null;
        }

    }

    @GetMapping("/user-info/{userIdx}")
    @ApiOperation(value = "유저에 등록된 기기정보", notes = "유저1 : 장치N")
    public ResponseEntity<List<UserInfoResponse>> infoFromUser(@PathVariable Long userIdx){
        return ResponseEntity.ok().body(deviceService.diabetesInfo(userIdx));
    }

    @GetMapping("/check-data/{userIdx}")
    @ApiOperation(value = "유저에 등록된 기기정보의 마지막 데이터", notes = "유저1 : 장치N")
    public ResponseEntity<WeakHashMap<String,Object>> checkData(@PathVariable Long userIdx){
        return ResponseEntity.ok().body(deviceService.getLastTimeData(userIdx));
    }


}
