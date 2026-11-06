package com.gdu.wacdo.validation;

import com.gdu.wacdo.dto.AssignmentRequestDTO;
import com.gdu.wacdo.validations.ValidAssignmentDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AssignmentDatesValidator
        implements ConstraintValidator<ValidAssignmentDates, AssignmentRequestDTO> {

    @Override
    public boolean isValid(
            AssignmentRequestDTO dto,
            ConstraintValidatorContext context
    ) {
        if (dto == null || dto.getStartDate() == null || dto.getEndDate() == null) {
            return true;
        }

        if (dto.getEndDate().isAfter(dto.getStartDate())) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(
                        "{validation.assignment.dates.invalid}"
                )
                .addPropertyNode("endDate")
                .addConstraintViolation();

        return false;
    }
}