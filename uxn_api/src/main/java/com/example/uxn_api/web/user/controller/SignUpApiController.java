package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_api.web.staff.dto.req.StaffSignUpReqDto;
import com.example.uxn_api.web.user.dto.req.AdminSignUpReqDto;
import com.example.uxn_api.web.user.dto.req.UserSignUpReqDto;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffRole;
import com.example.uxn_common.global.domain.user.Role;
import com.example.uxn_common.global.domain.user.User;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sign-up")
@RequiredArgsConstructor
@Slf4j
public class SignUpApiController {

    private final UserService userService;
    private final StaffService staffService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/user/save")
    @ApiOperation(value = "유저 등록")
    public ResponseEntity<Long> userSave(
            @RequestBody @Valid UserSignUpReqDto reqDto
            ){
        Staff staff = staffService.findByEmail(reqDto.getEmail());
        User chkUser = userService.findByEmail(reqDto.getEmail());
        if(staff == null && chkUser == null) {
            User user = User
                    .builder()
                    .userName(reqDto.getUserName())
                    .email(reqDto.getEmail())
                    .password(passwordEncoder.encode(reqDto.getPassword()))
                    .gender(reqDto.getGender())
                    .role(Role.USER)
                    .birth(reqDto.getBirth())
                    .enabled(true)
                    .authorities(reqDto.getAuthorities())
                    .build();
            userService.saveUser(user);
            userService.addAuthority(user.getIdx(), "ROLE_USER");
            return ResponseEntity.ok(user.getIdx());
        }else {
            if(staff!=null){
                if(!staff.isEmailVerifiedSuccess() && staff.getWillConfirmDate()==null && staff.getEmailVerifyStartTime()!=null){
                    LocalDateTime tmp = staff.getEmailVerifyStartTime().plusMinutes(30);
                    if(tmp.isBefore(LocalDateTime.now())){
                        log.info("회원 가입이 가능한 유일한 상태");
                        staffService.deleteStaff(staff);
                        User user = User
                                .builder()
                                .userName(reqDto.getUserName())
                                .email(reqDto.getEmail())
                                .password(passwordEncoder.encode(reqDto.getPassword()))
                                .gender(reqDto.getGender())
                                .role(Role.USER)
                                .birth(reqDto.getBirth())
                                .enabled(true)
                                .authorities(reqDto.getAuthorities())
                                .build();
                        userService.saveUser(user);
                        userService.addAuthority(user.getIdx(), "ROLE_USER");
                        return ResponseEntity.ok(user.getIdx());
                    }
                }
            } else if(chkUser != null){
                if(!chkUser.isEmailVerifiedSuccess() && chkUser.getWillConfirmDate()==null && chkUser.getEmailVerifyStartTime()!=null){
                    LocalDateTime tmp = chkUser.getEmailVerifyStartTime().plusMinutes(30);
                    if(tmp.isBefore(LocalDateTime.now())){
                        log.info("회원 가입이 가능한 유일한 상태");
                        userService.deleteUser(chkUser);
                        User user = User
                                .builder()
                                .userName(reqDto.getUserName())
                                .email(reqDto.getEmail())
                                .password(passwordEncoder.encode(reqDto.getPassword()))
                                .gender(reqDto.getGender())
                                .role(Role.USER)
                                .birth(reqDto.getBirth())
                                .enabled(true)
                                .authorities(reqDto.getAuthorities())
                                .build();
                        userService.saveUser(user);
                        userService.addAuthority(user.getIdx(), "ROLE_USER");
                        return ResponseEntity.ok(user.getIdx());
                    }
                }
            }
            log.info("이메일 중복");
            throw new LoginException("Duplicate Email", ErrorCode.DUPLICATE_ACCOUNT);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Exception Handler 만들어 줄것.
        }
    }

    @PostMapping("/admin/save")
    @ApiOperation(value = "관리자 등록")
    public ResponseEntity<User> adminSave(
            @RequestBody @Valid AdminSignUpReqDto reqDto
            ){
        Staff staff = staffService.findByEmail(reqDto.getEmail());
        User chkUser = userService.findByEmail(reqDto.getEmail());
        if(staff == null && chkUser == null){
            User user = User
                    .builder()
                    .userName(reqDto.getUserName())
                    .email(reqDto.getEmail())
                    .password(passwordEncoder.encode(reqDto.getPassword()))
                    .role(Role.ADMIN)
                    .gender(reqDto.getGender())
                    .enabled(true)
                    .authorities(reqDto.getAuthorities())
                    .build();
            userService.saveUser(user);
            userService.addAuthority(user.getIdx(), "ROLE_ADMIN");
            return ResponseEntity.ok(user);
        }else {
            log.info("이메일 중복");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PostMapping("/staff/save")
    @ApiOperation(value = "의사 등록")
    public ResponseEntity<Long> signUp(@RequestBody StaffSignUpReqDto reqDto){
        User user = userService.findByEmail(reqDto.getEmail());
        Staff chkStaff = staffService.findByEmail(reqDto.getEmail());
        if(user == null && chkStaff == null){
            Staff staff = Staff
                    .builder()
                    .staffName(reqDto.getStaffName())
                    .email(reqDto.getEmail())
                    .password(passwordEncoder.encode(reqDto.getPassword()))
                    .hospital(reqDto.getHospital())
                    .role(StaffRole.STAFF)
                    .enabled(true)
                    .authorities(reqDto.getAuthorities())
                    .birth(reqDto.getBirth())
                    .phoneNumber(reqDto.getPhoneNumber())
                    .gender(reqDto.getGender())
                    .approvalCode(reqDto.getApprovalCode())
                    .build();
            staffService.save(staff);
            staffService.addAuthority(staff.getIdx(), "ROLE_STAFF");
            return ResponseEntity.ok().body(staff.getIdx());
        }else {
            if(chkStaff!=null){
                if(!chkStaff.isEmailVerifiedSuccess() && chkStaff.getWillConfirmDate()==null && chkStaff.getEmailVerifyStartTime()!=null){
                    LocalDateTime tmp = chkStaff.getEmailVerifyStartTime().plusMinutes(30);
                    if(tmp.isBefore(LocalDateTime.now())){
                        log.info("회원 가입이 가능한 유일한 상태");
                        staffService.deleteStaff(chkStaff);
                        Staff staff = Staff
                                .builder()
                                .staffName(reqDto.getStaffName())
                                .email(reqDto.getEmail())
                                .password(passwordEncoder.encode(reqDto.getPassword()))
                                .hospital(reqDto.getHospital())
                                .role(StaffRole.STAFF)
                                .enabled(true)
                                .authorities(reqDto.getAuthorities())
                                .birth(reqDto.getBirth())
                                .phoneNumber(reqDto.getPhoneNumber())
                                .gender(reqDto.getGender())
                                .approvalCode(reqDto.getApprovalCode())
                                .build();
                        staffService.save(staff);
                        staffService.addAuthority(staff.getIdx(), "ROLE_STAFF");
                        return ResponseEntity.ok().body(staff.getIdx());
                    }
                }
            } else if(user != null){
                if(!user.isEmailVerifiedSuccess() && user.getWillConfirmDate()==null && user.getEmailVerifyStartTime()!=null){
                    LocalDateTime tmp = user.getEmailVerifyStartTime().plusMinutes(30);
                    if(tmp.isBefore(LocalDateTime.now())){
                        log.info("회원 가입이 가능한 유일한 상태");
                        userService.deleteUser(user);
                        Staff staff = Staff
                                .builder()
                                .staffName(reqDto.getStaffName())
                                .email(reqDto.getEmail())
                                .password(passwordEncoder.encode(reqDto.getPassword()))
                                .hospital(reqDto.getHospital())
                                .role(StaffRole.STAFF)
                                .enabled(true)
                                .authorities(reqDto.getAuthorities())
                                .birth(reqDto.getBirth())
                                .phoneNumber(reqDto.getPhoneNumber())
                                .gender(reqDto.getGender())
                                .approvalCode(reqDto.getApprovalCode())
                                .build();
                        staffService.save(staff);
                        staffService.addAuthority(staff.getIdx(), "ROLE_STAFF");
                        return ResponseEntity.ok().body(staff.getIdx());
                    }
                }
            }
            log.info("이메일 중복");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    ///api/v1/sign-up
    @PostMapping("/withdrawal/user/{email}")
    @ApiOperation(value = "회원 탈퇴")
    public ResponseEntity<Long> withdrawalUser(@PathVariable String email){
        log.debug("withdrawal : " + email);
        User user = userService.findByEmail(email);
        if(user!=null){
            log.debug("user found, start withdrawal");
            userService.withdrawal(email);
            return ResponseEntity.ok().body(200L);
        } else {
            Staff staff = staffService.findByEmail(email);
            if(staff!=null){
                log.debug("staff found, start withdrawal");
                staffService.deleteStaff(staff);
                return ResponseEntity.ok().body(200L);
            }
        }
        return ResponseEntity.status(400).build();
    }
}
