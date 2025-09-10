package ru.practicum.main.service.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SizeAfterTrimValidator.class)
public @interface SizeAfterTrim {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "string size after trim should be between {min} and {max} included";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
