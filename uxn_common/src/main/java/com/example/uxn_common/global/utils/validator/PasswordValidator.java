package com.example.uxn_common.global.utils.validator;

import com.example.uxn_common.global.utils.annotation.Password;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.MessageFormat;

@Component
public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final int MIN_SIZE = 8;
    private static final int MAX_SIZE = 36;
    private static final String regexPassword = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@%$%^&*()+=._-])[A-Za-z[0-9]!@%$%^&*()+=._-]{" + MIN_SIZE
            + "," + MAX_SIZE + "}$"; // 사용 가능한 특수문자 : $@$!%*#?&.

    @Override
    public void initialize(Password constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean isValidPassword = password.matches(regexPassword);
        if (!isValidPassword) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageFormat.format("영문자, 숫자, 특수문자를 포함한 8자이상 36자 미만의 비밀번호를 입력해주세요(허용된 특수문자 :!@%$%^&*()+=._-) ", MIN_SIZE, MAX_SIZE))
                    .addConstraintViolation();
        }
        return isValidPassword;
    }
}
