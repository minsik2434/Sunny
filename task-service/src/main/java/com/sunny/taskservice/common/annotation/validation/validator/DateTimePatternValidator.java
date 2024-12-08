package com.sunny.taskservice.common.annotation.validation.validator;

import com.sunny.taskservice.common.annotation.validation.DateTimePattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class DateTimePatternValidator implements ConstraintValidator<DateTimePattern, String> {
    private String datetimePattern;
    @Override
    public void initialize(DateTimePattern constraintAnnotation) {
        this.datetimePattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return Pattern.matches(datetimePattern, value);
    }
}
