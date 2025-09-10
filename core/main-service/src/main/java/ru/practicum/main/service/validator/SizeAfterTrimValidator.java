package ru.practicum.main.service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SizeAfterTrimValidator implements ConstraintValidator<SizeAfterTrim, String> {
    private int min;
    private int max;

    @Override
    public void initialize(SizeAfterTrim constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int sizeTrimmed = value.trim().length();

        return sizeTrimmed >= min && sizeTrimmed <= max;
    }
}
