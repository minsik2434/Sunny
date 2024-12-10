package com.sunny.taskservice.common.annotation.validation;

import com.sunny.taskservice.common.annotation.validation.validator.DateTimePatternValidator;
import com.sunny.taskservice.common.annotation.validation.validator.UniqueElementsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueElementsValidator.class)
public @interface UniqueElements {
    String message() default "Elements of this Collection must not be duplicated";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
