package com.example.uxn_common.global.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodeType {
    USER_ROLE(1000,"USER_ROLE"),
    CONNECTION_TYPE(1100,"접속 종류"),
    LOGIN_TYPE(1200,"로그인 유형"),
    VALUE_TYPE(1300,"측정값 종류"),
    EVENT_TYPE(1400,"이벤트 분류");

    private int id;
    private String name;
}
