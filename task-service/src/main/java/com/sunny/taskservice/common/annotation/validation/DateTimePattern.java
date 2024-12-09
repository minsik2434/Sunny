package com.sunny.taskservice.common.annotation.validation;

import com.sunny.taskservice.common.annotation.validation.validator.DateTimePatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateTimePatternValidator.class)
public @interface DateTimePattern {
    String message() default "Invalid datetime Format yyyy-MM-dd HH:mm:ss";
    String pattern() default "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
