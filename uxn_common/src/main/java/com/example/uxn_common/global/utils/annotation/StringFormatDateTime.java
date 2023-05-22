package com.example.uxn_common.global.utils.annotation;

import com.example.uxn_common.global.utils.validator.StringFormatValidator;
import org.jboss.jandex.AnnotationTarget;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Constraint(validatedBy = StringFormatValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringFormatDateTime { // String 으로 입력 받으면 날자 / 시간 계산이 안됨.
    String message() default "형식에 맞지 않습니다.";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String pattern() default "yyyy-MM-dd HH:mm:ss";
}
