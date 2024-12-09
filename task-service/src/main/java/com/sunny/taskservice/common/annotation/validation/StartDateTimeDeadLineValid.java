package com.sunny.taskservice.common.annotation.validation;

import com.sunny.taskservice.common.annotation.validation.validator.StartDateTImeDeadLineValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {StartDateTImeDeadLineValidator.class})
public @interface StartDateTimeDeadLineValid {

    String message() default "The start time cannot be earlier than the due date.";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String startDateTime();
    String deadLine();
}
