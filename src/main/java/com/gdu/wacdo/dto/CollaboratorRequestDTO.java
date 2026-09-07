package com.gdu.wacdo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CollaboratorRequestDTO {
    @NotBlank(message = "{validation.collaborator.name.not-blank}")
    private String name;

    @NotBlank(message = "{validation.collaborator.first-name.not-blank}")
    private String firstName;

    @NotBlank(message = "{validation.collaborator.email.not-blank}")
    @Email(message = "{validation.collaborator.email.invalid}")
    private String email;

    @NotNull(message = "{validation.collaborator.first-hire-date.not-null}")
    @PastOrPresent(message = "{validation.collaborator.first-hire-date.invalid}")
    private LocalDate firstHireDate;

    private boolean admin;

    @NotBlank(message = "{validation.collaborator.password.not-blank}")
    @Size(
            min = 8,
            message = "{validation.collaborator.password.size}"
    )
    private String password;
}