package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentRequestDTO {

    private LocalDate startDate;
    private LocalDate endDate;
}