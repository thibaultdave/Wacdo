package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollaboratorResponseDTO {
    private Long id;
    private String name;
    private String firstName;
    private String email;
    private LocalDate firstHireDate;
    private boolean admin;
}