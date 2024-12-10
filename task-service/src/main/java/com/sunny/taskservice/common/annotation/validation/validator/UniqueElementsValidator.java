package com.sunny.taskservice.common.annotation.validation.validator;

import com.sunny.taskservice.common.annotation.validation.UniqueElements;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UniqueElementsValidator implements ConstraintValidator<UniqueElements, Collection<?>> {

    @Override
    public boolean isValid(Collection<?> objects, ConstraintValidatorContext constraintValidatorContext) {
        Set<?> collect = new HashSet<>(objects);
        return collect.size() == objects.size();
    }
}
