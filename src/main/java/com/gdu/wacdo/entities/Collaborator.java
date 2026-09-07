package com.gdu.wacdo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String firstName;

    @NotBlank
    @Email
    private String email;

    @PastOrPresent
    private LocalDate firstHireDate;

    private boolean isAdmin;

    @NotBlank
    private String password;

    @OneToMany(mappedBy = "collaborator")
    private List<Assignment> assignments = new ArrayList<>();
}