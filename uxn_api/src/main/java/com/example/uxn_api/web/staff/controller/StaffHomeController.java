package com.example.uxn_api.web.staff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class StaffHomeController {

    @GetMapping("/greeting")
    public String staffGreeting(){

        return "hello";
    }
}
