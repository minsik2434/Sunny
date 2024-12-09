package com.sunny.taskservice.common.annotation.validation.validator;

import com.sunny.taskservice.common.annotation.validation.StartDateTimeDeadLineValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class StartDateTImeDeadLineValidator implements ConstraintValidator<StartDateTimeDeadLineValid, Object> {
    private String startDateTimeField;
    private String deadLineField;
    @Override
    public void initialize(StartDateTimeDeadLineValid constraintAnnotation) {
        this.startDateTimeField = constraintAnnotation.startDateTime();
        this.deadLineField = constraintAnnotation.deadLine();
    }
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        try{
            LocalDateTime startTime = getFieldDataLocalDateTime(o, startDateTimeField);
            LocalDateTime endTime = getFieldDataLocalDateTime(o, deadLineField);
            if(startTime == null || endTime == null){
                // null 값은 검증하지 않음
                return true;
            }
            return !startTime.isAfter(endTime);
        }catch(DateTimeParseException e){
            //DateTimeParseException 발생시 검증하지 않음
            return true;
        }
    }

    private LocalDateTime getFieldDataLocalDateTime(Object o, String fieldName) {
        Class<?> clazz = o.getClass();
        Field dateField;
        try{
            dateField = clazz.getDeclaredField(fieldName);
            dateField.setAccessible(true);
            Object target = dateField.get(o);
            if (target == null) {
                return null;
            }
            return getLocalDate(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDateTime getLocalDate(Object target){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse((String) target,formatter);
    }
}
