package com.gdu.wacdo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollaboratorRequestDTO {
    private String name;
    private String firstName;
    private String email;
    private LocalDate firstHireDate;
    private boolean admin;
    private String password;
}