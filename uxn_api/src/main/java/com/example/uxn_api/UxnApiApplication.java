package com.example.uxn_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@EnableScheduling
@SpringBootApplication
public class UxnApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(UxnApiApplication.class, args);
    }

}
