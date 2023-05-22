package com.example.uxn_api.web.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginException extends RuntimeException{

    private ErrorCode errorCode;

    public LoginException(String message, ErrorCode errorCode){
        super(String.valueOf(errorCode.getStatus()));
        this.errorCode = errorCode;
    }
}