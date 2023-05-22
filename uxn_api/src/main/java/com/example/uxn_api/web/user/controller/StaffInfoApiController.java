package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.staff.dto.req.UserRegistrationDto;
import com.example.uxn_api.web.staff.dto.res.*;
import com.example.uxn_api.web.staff.dto.res.UnRecognizeList;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/staff-info")
public class StaffInfoApiController {

    private final StaffService staffService;
    private final MailSendService mailSendService;

    private final UserService userService;

    @GetMapping("/list")
    @ApiOperation(value = "의사 목록", notes = "의사 목록을 보고 환자가 의사를 선택")
    public ResponseEntity<List<StaffListResDto>> staffList(){
        log.debug("staff list :");
        return ResponseEntity.ok(staffService.staffList());
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "의사 상세정보", notes = "의사 클릭시 의사에 대한 상세정보 제공")
    @ApiImplicitParam(name = "id", value = "의사 index", required = true)
    public ResponseEntity<StaffResDto> staffInfoResponse(@PathVariable Long id){
        return ResponseEntity.ok(staffService.staffDetail(id));
    }

    @GetMapping("/staff-detail/{id}")
    @ApiOperation(value = "의사 상세정보", notes = "유저 정보로 내가 등록한 의사 정보 조회")
    @ApiImplicitParam(name = "id", value = "유저 index", readOnly = true)
    public ResponseEntity<List<StaffResDto>> staffInfoResDto(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.staffDetailForUser(id));
    }

    @GetMapping("/send-mail/{userIdx}")
    @ApiOperation(value = "등록 시 메일 발송", notes = "리스트에서 의사 등록버튼 눌렀을 시 메일 발송, 링크 클릭을 안한(승인이 안된) 목록 필요.")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "email", value = "의사 email", required = true),
                    @ApiImplicitParam(name = "idx", value = "환자 idx", required = true)
            }
    )
    public void sendLink(String email, @PathVariable Long userIdx) throws MessagingException {
        log.debug("이메일 확인 :" + email);
        log.debug("idx 확인 :" + userIdx);
        Staff staff = staffService.findByEmail(email);
        User user = userService.findUser(userIdx);
        if(staff != null) {


            mailSendService.verificationMailSend(email, 3, userIdx, null);
        }
    }



    @GetMapping("/un-recognize/list/{staffId}")
    public ResponseEntity<List<UnRecognizeList>> unRecognizeList( // 병원 계정으로 등록 요청이 온 승인이 안된 환자 리스트
            @PathVariable Long staffId
    ) {
        return ResponseEntity.ok(staffService.unRecognizeList(staffId));
    }

    @GetMapping("/recognize/{idx}/{staff}")
    @ApiOperation(value = "승인이 안된 환자 목록을 보여주고 승인 버튼을 누르면 승인 완료.", notes = "이메일 링크 클릭 으로도 승인 가능하고 해당 api 를 통해 승인 버튼을 눌러도 승인 가능.")
    @ApiImplicitParam(name = "idx", value = "환자 idx", required = true)
    public void recognize (@PathVariable Long idx, @PathVariable Long staff){
        try{
            staffService.checkLink(staff, idx);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    @PutMapping("/registration")
    @ApiOperation(value = "의사 등록", notes = "환자 -> 의사 등록")
    public ResponseEntity<UserRegistrationDto> userRegist(@RequestBody UserRegistrationDto registrationDto){
        UserRegistrationDto dto = staffService.registration(registrationDto,false);

        return ResponseEntity.ok(dto);

    }

    @PutMapping("/remove/registration")
    @ApiOperation(value = "의사 해제", notes = "환자 -> 의사 해제")
    public void userRemoveRegist(@RequestBody UserRegistrationDto registrationDto){
        Staff staff = staffService.detailStaff(registrationDto.getStaffIdx());
//        staffService.plusUserCount(registrationDto.getStaffIdx());
        staffService.deRegistrationUser(registrationDto.getUserIdx(), registrationDto.getStaffIdx());


    }

}
