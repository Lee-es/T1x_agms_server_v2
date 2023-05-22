package com.example.uxn_api.web.invite;

import com.example.uxn_api.config.CommonConstant;
import com.example.uxn_api.service.staff.StaffService;
import com.example.uxn_api.service.user.UserService;
//import com.example.uxn_api.web.device.dto.res.UserInfoResponse;
import com.example.uxn_common.global.domain.email.Invite;
import com.example.uxn_common.global.domain.email.repository.InviteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
//import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.User;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/invited")
@RequiredArgsConstructor
@Slf4j
public class InviteController {


    private final InviteRepository inviteRepository;

//    private final StaffRepository staffRepository;

    private final UserService userService;

    private final StaffService staffService;

    @GetMapping("/{code}")
    @ApiOperation(value = "초대 링크 클릭", notes = " 로그인 확인 -> 로그인 안되어있으면 로그인 페이지로 리다이렉트, 로그인 되면 코드 실행 후 alert 페이지로 이동")
    public String infoFromUser(@PathVariable String code) throws UnsupportedEncodingException {
        log.debug("code:"+code);
        if(StringUtils.hasText(code)){
            log.debug("code exist:"+code);
            Invite invite = inviteRepository.findByCode(code);
            try{
                if(invite!=null){
                    log.debug("invite not null");
                    Staff staff = invite.getStaff();
                    if(staff!=null){
                        log.debug("staff not null");
                        User user = userService.findUser(invite.getIdx());
                        if(user!=null){
                            log.debug("user not null");
                            staffService.registration(staff.getEmail(), user.getIdx(),true);
                            return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("의사를 등록하였습니다.","UTF-8");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return "redirect:" + CommonConstant.FRONT_HOST+ "/alert?message="+ URLEncoder.encode("유효하지 않은 코드입니다.","UTF-8");

//        if(user.getWillConfirmDate()!=null){
//            userService.emailCheck(email);
//            return "redirect:http://3.34.218.91/alert?message="+ URLEncoder.encode("인증되었습니다.","UTF-8");
//        } else {
//            if(LocalDateTime.now().isBefore(date.plusMinutes(MAX_EXPIRE_TIME))){ // 링크를 보낸 시간이 현재 링크를 클릭한 시간 +30분 보다 이전인가?
//                userService.emailCheck(email); // 30분이 지나면 인증 실패처리 -> 30분 전에 클릭하면 업데이트 / 30분이 지나면 업데이트 x
//                return "redirect:http://3.34.218.91/alert?message="+ URLEncoder.encode("계정 생성 인증이 완료 되었습니다.","UTF-8");
//            } else {
//                return "redirect:http://3.34.218.91/alert?message="+ URLEncoder.encode("인증시간이 초과되었습니다. 다시 회원가입을 해 주십시오.","UTF-8");
//            }
//        }
    }
}
