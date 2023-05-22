package com.example.uxn_api.config;

import com.example.uxn_common.UxnCommonApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
@Configuration
@ComponentScans({
        @ComponentScan(basePackageClasses = UxnCommonApplication.class)})
public class ComponentConfig {

}
