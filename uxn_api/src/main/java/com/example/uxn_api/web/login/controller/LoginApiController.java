package com.example.uxn_api.web.login.controller;

import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.web.login.dto.req.LoginReqDto;
import com.example.uxn_common.global.domain.Login;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/login")
@RestController
@RequiredArgsConstructor
@Slf4j
//사용하지 않는 class
//JWTLoginFilter class를 통해 인증 및 토큰 발급으로 로그인 처리
public class LoginApiController {


    private final LoginService loginService;

//    @PostMapping("/staff")
//    @ApiOperation(value = "의사 로그인 토큰 발급")
//    public void staffLogin(@RequestBody LoginReqDto reqDto){
//        loginService.staffLogin(reqDto);
//    }
//
//    @PostMapping("/user")
//    @ApiOperation(value = "유저 로그인 토큰 발급")
//    public void userLogin(@RequestBody LoginReqDto reqDto){
//        loginService.userLogin(reqDto);
//    }

    @PostMapping("")
    @ApiOperation(value = "통합 로그인", notes = "이메일 아이디를 사용 하기떄문에 따로 분리 x")
    public void login(@RequestBody LoginReqDto reqDto) { // inputStream 으로 LoginReqDto 로 들어오는 값을 바로 읽어서 UserDetailsService 를 상속한 loadUserByUsername 메소드로 바로 처리됨.

        log.debug("check pared here");
    }

    @GetMapping("/verify")
    public ResponseEntity<Login> loginVerify(@RequestBody LoginReqDto dto) {
//        loginService.userVerify(dto);
        log.info("컨트롤러");
        Login login = loginService.userVerify(dto.getUserId(), dto.getPassword());
        return ResponseEntity.ok().body(login);
    }



}
