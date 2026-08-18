package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentResponseDTO {
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
}