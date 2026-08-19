package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentResponseDTO {
    private Long id;
    private Long collaboratorId;
    private Long restaurantId;
    private Long jobId;
    private LocalDate startDate;
    private LocalDate endDate;
}