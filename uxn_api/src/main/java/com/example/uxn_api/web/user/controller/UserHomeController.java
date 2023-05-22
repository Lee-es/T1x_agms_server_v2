package com.example.uxn_api.web.user.controller;

import com.example.uxn_api.config.CommonConstant;
import com.example.uxn_api.service.login.LoginService;
import com.example.uxn_api.web.login.dto.res.LoginCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserHomeController {

    private final LoginService loginService;


    @GetMapping("/check")
    @ResponseBody
    public LoginCheckResult checkLogin(HttpServletRequest request, @RequestParam(required = false) String device
    ) {
        log.debug("checkLogin 65 device:"+device);

        return loginService.checkLogin(request,device,null);

    }
    @GetMapping("/confirm")
    @ResponseBody
    public LoginCheckResult confirmLogin(
            HttpServletRequest request, @RequestParam(required = false) String device
    ) {

        return loginService.confirmLogin(request,device);

    }

    @GetMapping("/login")
    public String redirectLogin(
            HttpServletRequest request
    ) {

        return "redirect:" + CommonConstant.FRONT_HOST+ "/login";

    }
}
