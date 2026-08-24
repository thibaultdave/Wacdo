package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollaboratorSummaryDTO {
    private Long id;
    private String name;
    private String firstName;
    private String email;
    private LocalDate firstHireDate;
    private boolean admin;
}