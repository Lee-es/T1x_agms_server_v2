package com.example.uxn_common.global.domain;

import com.example.uxn_common.global.entity.BaseTimeEntity;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class Login extends BaseTimeEntity implements UserDetails {

    private String userId;

    private String userName;

    private String password;

    private String email;
}
