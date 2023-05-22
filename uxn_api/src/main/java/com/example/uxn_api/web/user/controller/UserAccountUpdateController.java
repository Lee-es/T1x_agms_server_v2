package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.login.dto.req.ChangePasswordDto;
import com.example.uxn_api.web.login.dto.req.LoginDto;
import com.example.uxn_common.global.domain.user.ActivityKind;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/account")
@RequiredArgsConstructor
public class UserAccountUpdateController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    private final LogService logService;

    @PatchMapping("/password-update/{email}")
    @ApiOperation(value = "비밀번호 변경", notes = "영문자, 숫자, 특수문자를 포함한 8자이상 36자 미만의 비밀번호를 입력해주세요(허용된 특수문자 :!@%$%^&*()+=._-)")
    public void updatePassword(@PathVariable String email, @RequestBody ChangePasswordDto req){
        logService.log(true, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경 시도", email,null,null,null);
        userService.updatePassword(email, passwordEncoder.encode(req.getPassword()), req.getToken());
    }

}
