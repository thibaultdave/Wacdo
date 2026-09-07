package com.gdu.wacdo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequestDTO {
    @NotBlank(message = "{validation.job.name.not-blank}")
    private String name;
}