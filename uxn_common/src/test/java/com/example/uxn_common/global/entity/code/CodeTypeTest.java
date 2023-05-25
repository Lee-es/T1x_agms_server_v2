package com.example.uxn_common.global.entity.code;

import com.example.uxn_common.global.domain_new.code.CodeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CodeTypeTest {
    @Test
    void codeTypeTest(){

        Assertions.assertEquals(CodeType.USER_ROLE.getId(), 1000);
        Assertions.assertEquals(CodeType.CONNECTION_TYPE.getId(), 1100);
        Assertions.assertEquals(CodeType.LOGIN_TYPE.getId(), 1200);
        Assertions.assertEquals(CodeType.VALUE_TYPE.getId(), 1300);
        Assertions.assertEquals(CodeType.EVENT_TYPE.getId(), 1400);

    }
}