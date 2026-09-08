package com.gdu.wacdo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentRequestDTO {
    @NotNull(message = "{validation.assignment.collaborator-id.not-null}")
    @Positive(message = "{validation.assignment.collaborator-id.positive}")
    private Long collaboratorId;

    @NotNull(message = "{validation.assignment.restaurant-id.not-null}")
    @Positive(message = "{validation.assignment.restaurant-id.positive}")
    private Long restaurantId;

    @NotNull(message = "{validation.assignment.job-id.not-null}")
    @Positive(message = "{validation.assignment.job-id.positive}")
    private Long jobId;

    @NotNull(message = "{validation.assignment.start-date.not-null}")
    @PastOrPresent(message = "{validation.assignment.start-date.invalid}")
    private LocalDate startDate;

    @PastOrPresent(message = "{validation.assignment.end-date.invalid}")
    private LocalDate endDate;
}