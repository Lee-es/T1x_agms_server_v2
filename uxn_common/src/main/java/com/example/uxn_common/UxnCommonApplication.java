package com.example.uxn_common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class UxnCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(UxnCommonApplication.class, args);
    }

}
