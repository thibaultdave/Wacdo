package com.gdu.wacdo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ValidationErrorResponseDTO {

    private int status;
    private Map<String, String> errors;
}