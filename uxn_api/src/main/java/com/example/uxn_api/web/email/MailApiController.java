package com.example.uxn_api.web.email;

import com.example.uxn_api.config.CommonConstant;
import com.example.uxn_api.service.email.MailSendService;
import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_common.global.domain.email.Invitation;
import com.example.uxn_common.global.domain.email.repository.InvitationRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.ActivityKind;
import com.example.uxn_common.global.domain.user.ChangePasswordToken;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.ChangePasswordTokenRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
@Slf4j
public class MailApiController {
    private final UserService userService;
    private final StaffService staffService;

    private final MailSendService mailSendService;

    private final InvitationRepository inviteRepository;
    private final ChangePasswordTokenRepository changePasswordTokenRepository;

    private static final Long MAX_EXPIRE_TIME = 30L;
//    private static final Long MAX_EXPIRE_TIME = 1L;

    private final LogService logService;


    @PutMapping("/send")
    @ResponseBody
    @ApiOperation(value = "인증 코드 발송", notes = "이메일을 파라미터로 받고 메일 인증코드를 받아서 등록.")
    public void mailSend(String email) throws MessagingException {
        User user = userService.findByEmail(email);
        System.out.println("이메일 : " + email);

        if(user != null) {
            System.out.println("유저");
            int index = user.getId();
            String code = MailSendService.createKey();
            mailSendService.verificationMailSend(email,1, index,code);
            userService.emailVerifyCode(code,email);
            logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);
        }
//        else if (staff != null){
//            System.out.println("스태프");
//            Long index = staff.getIdx();
//            String code = MailSendService.createKey();
//            mailSendService.verificationMailSend(email,1,index,code);
//            staffService.emailVerifyCode(code,email);
//            logService.log(false, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증", email,null,null,null);
//        }
        else{
            logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증, user not found", email,null,null,null);
        }
    }


    @GetMapping("/confirm/{email}/{code}")
    @ApiOperation(value = "이메일 인증 링크", notes = "링크 발송")
    public String confirmEmail(@PathVariable String email, @PathVariable String code) throws UnsupportedEncodingException {
        User user = userService.findByEmail(email);
//        Staff staff = staffService.findByEmail(email);

        if (user != null && user.getEmailVerifyCode()!=null && user.getEmailVerifyCode().equals(code)){
            LocalDateTime date = user.getCreatedAt();
            if(user.getExpiredAt()!=null){
                userService.emailCheck(email);
                logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공", email,code,null,null);
                return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("인증되었습니다.","UTF-8");
            } else {
                if(LocalDateTime.now().isBefore(date.plusMinutes(MAX_EXPIRE_TIME))){ // 링크를 보낸 시간이 현재 링크를 클릭한 시간 +30분 보다 이전인가?
                    userService.emailCheck(email); // 30분이 지나면 인증 실패처리 -> 30분 전에 클릭하면 업데이트 / 30분이 지나면 업데이트 x
                    logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공, create account finish", email,code,null,null);
                    return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("계정 생성 인증이 완료 되었습니다.","UTF-8");
                } else {
                    logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공, but time over", email,code,null,null);
                    return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("인증시간이 초과되었습니다. 다시 회원가입을 해 주십시오.","UTF-8");
                }
            }


        }
//        else if(staff != null && Objects.equals(staff.getEmailVerifyCode(), code)){
//            LocalDateTime sendLinkTime = staff.getEmailVerifyStartTime();
//            if(staff.getWillConfirmDate()!=null){
//                staffService.emailCheck(email); // 30분이 지나면 인증 실패처리 -> 30분 전에 클릭하면 업데이트 / 30분이 지나면 업데이트 x
//                logService.log(false, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공", email,code,null,null);
//                return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("인증되었습니다.","UTF-8");
//            } else {
//                if(LocalDateTime.now().isBefore(sendLinkTime.plusMinutes(MAX_EXPIRE_TIME))){ // 링크를 보낸 시간이 현재 링크를 클릭한 시간 +30분 보다 이전인가?
//                    staffService.emailCheck(email); // 30분이 지나면 인증 실패처리 -> 30분 전에 클릭하면 업데이트 / 30분이 지나면 업데이트 x
//                    logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공, create account finish", email,null,null,null);
//                    return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("계정 생성 인증이 완료 되었습니다.","UTF-8");
//                } else {
//                    logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 성공, but time over", email,code,null,null);
//                    return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("인증시간이 초과되었습니다. 다시 회원가입을 해 주십시오.","UTF-8");
//                }
//            }
//
//
//        }
        else {
            logService.log(true, ActivityKind.SEND_VERIFICATION_EMAIL,"이메일 인증 실패", email,code,null,null);
            return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("인증을 할 수 없습니다.","UTF-8");
        }
    }

    @GetMapping("/check-mail/{idx}/{email}")
    @ApiOperation(value = "링크 클릭시 등록 확인", notes = "환자의 등록 요청메일이 오고 의사가 메일 확인을 하여 링크 클릭시 환자 등록.")
    public String checkMail(@PathVariable Integer idx,
                          @PathVariable String email)  throws UnsupportedEncodingException {

        String error = staffService.checkLink(email, idx);
        if(error!=null){
            return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode(error,"UTF-8");
        } else {
            return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("승인하였습니다.","UTF-8");
        }

    }

    @GetMapping("/link")
    @ResponseBody
    @ApiOperation(value = "비밀번호 재설정 링크", notes = "해당 링크를 통해 비밀번호를 재설정 할 수 있는 링크를 전달.")
    public void passwordLink(String email) throws MessagingException {
        log.debug("passwordLink 비밀번호 재설정 링크 전송 시작 ");
        User user = userService.findByEmail(email);

        String key = MailSendService.createKey();
        changePasswordTokenRepository.save(ChangePasswordToken.builder()
                .created(LocalDateTime.now())
                .email(email)
                .token(key)
                .build());

        if(user != null){
            Integer idx = user.getId();
            mailSendService.verificationMailSend(email,2, idx,key);
            logService.log(true, ActivityKind.FIND_PASSWORD,"비밀번호 재설정 링크 전송", email,null,null,null);
        }
        else
        {
            logService.log(true, ActivityKind.FIND_PASSWORD,"user not found fail", email,null,null,null);
            throw new LoginException("not found user", ErrorCode.NOT_FOUND);
        }
    }


    @PutMapping("/invite")///api/v1/mail
    @ResponseBody
    @ApiOperation(value = "초대 메일 발송 ", notes = "회원 가입 후 발송하는 이메일과 비슷 한 로직,  ")
    public void mailSendForInvite(Integer staffIdx, String staffEmail,String userEmail) throws MessagingException {
        User staff = null;
        User user = userService.findByEmail(userEmail);

        if(staffEmail!=null){
            staff = userService.findByEmail(staffEmail);
        } else if(staffIdx!=null){
            staff = userService.findByIdx(staffIdx);
        }

        //user staff 맵핑관계 확인 필요
        if(staff==null){
            logService.log(true, ActivityKind.INVITE_USER,"staff not found fail", staffEmail,String.valueOf(staffIdx),userEmail,null);
            throw new LoginException("STAFF_NOT_FOUND", ErrorCode.STAFF_NOT_FOUND);//411
        }
        if(user==null){
            logService.log(true, ActivityKind.INVITE_USER,"user not found fail", staffEmail,String.valueOf(staffIdx),userEmail,null);
            throw new LoginException("USER_NOT_FOUND", ErrorCode.NOT_FOUND);//404
        }

        String key = MailSendService.createKey();
        Invitation invite = Invitation.builder()
                .patient_id(user.getId())
                .code(key).staff_id(staff.getId()).build();
        inviteRepository.save(invite);
        mailSendService.verificationMailSend(userEmail,4,0, key);
        logService.log(true, ActivityKind.INVITE_USER,"send email invite", staffEmail,String.valueOf(staffIdx),userEmail,null);
    }

}
