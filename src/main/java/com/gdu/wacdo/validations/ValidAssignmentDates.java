package com.gdu.wacdo.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.gdu.wacdo.validation.AssignmentDatesValidator;

import java.lang.annotation.*;

@Constraint(validatedBy = AssignmentDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAssignmentDates {

    String message() default "{validation.assignment.dates.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}