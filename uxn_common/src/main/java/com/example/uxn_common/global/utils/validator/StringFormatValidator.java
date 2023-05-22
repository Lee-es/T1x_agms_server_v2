package com.example.uxn_common.global.utils.validator;

import com.example.uxn_common.global.utils.annotation.StringFormatDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringFormatValidator implements ConstraintValidator<StringFormatDateTime, String> {
    private String pattern;

    @Override
    public void initialize(StringFormatDateTime constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
