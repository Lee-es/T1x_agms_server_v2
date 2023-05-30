package com.example.uxn_common.global.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Code {
    ROLE_USER(1001,"ROLE_USER", CodeType.USER_ROLE.getId()),
    ROLE_STAFF(1002,"ROLE_STAFF",CodeType.USER_ROLE.getId()),
    ROLE_ADMIN(1003,"ROLE_ADMIN",CodeType.USER_ROLE.getId()),

    MOBILE_APP(1101,"MOBILE_APP",CodeType.CONNECTION_TYPE.getId()),
    WEB_REPORT(1102,"WEB_REPORT",CodeType.CONNECTION_TYPE.getId()),
    MOBILE_REPORT(1103,"MOBILE_REPORT",CodeType.CONNECTION_TYPE.getId()),

    NONE(1201,"TRY_LOGIN",CodeType.LOGIN_TYPE.getId()),
    TRY_LOGIN(1202,"LOGIN_SUCCESS",CodeType.LOGIN_TYPE.getId()),
    LOGIN_FAIL(1203,"LOGIN_FAIL",CodeType.LOGIN_TYPE.getId()),
    LOGIN_SUCCESS(1204,"WEB",CodeType.LOGIN_TYPE.getId()),
    INVITE_USER(1205,"TRY_LOGIN",CodeType.LOGIN_TYPE.getId()),
    SEND_VERIFICATION_EMAIL(1206,"LOGIN_SUCCESS",CodeType.LOGIN_TYPE.getId()),
    FIND_PASSWORD(1207,"LOGIN_FAIL",CodeType.LOGIN_TYPE.getId()),
    CHANGE_PASSWORD(1208,"WEB",CodeType.LOGIN_TYPE.getId()),

    WE_CURRENT(1301,"WE_CURRENT",CodeType.VALUE_TYPE.getId()),
    WE_POTENTIAL(1302,"WE_POTENTIAL",CodeType.VALUE_TYPE.getId()),
    AE_CURRENT(1303,"AE_CURRENT",CodeType.VALUE_TYPE.getId()),
    AE_POTENTIAL(1304,"AE_POTENTIAL",CodeType.VALUE_TYPE.getId()),
    RE_POTENTIAL(1305,"RE_POTENTIAL",CodeType.VALUE_TYPE.getId()),
    BATTERY_LEVEL(1306,"BATTERY_LEVEL",CodeType.VALUE_TYPE.getId()),

    MEAL(1401,"식사",CodeType.EVENT_TYPE.getId()),
    EXERCISE(1402,"운동",CodeType.EVENT_TYPE.getId()),
    ACTIVITY(1403,"활동",CodeType.EVENT_TYPE.getId());

    private int id;
    private String name;
    private int type;

}