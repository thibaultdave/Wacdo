package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentResponseDTO {
    private Long id;
    private CollaboratorSummaryDTO collaborator;
    private RestaurantSummaryDTO restaurant;
    private JobResponseDTO job;
    private LocalDate startDate;
    private LocalDate endDate;
}