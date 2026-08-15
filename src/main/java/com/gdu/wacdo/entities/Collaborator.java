package com.gdu.wacdo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    @Email
    private String email;
    @Past
    private LocalDate firstHireDate;
    @NotNull
    private boolean isAdmin;
    @NotBlank
    private String password;
    @OneToMany(mappedBy = "collaborator")
    private List<Assignment> assignments = new ArrayList<>();
}