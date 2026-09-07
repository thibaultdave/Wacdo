package com.gdu.wacdo.exceptions;

import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import com.gdu.wacdo.dto.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorResponseBuilder errorResponseBuilder;

    public GlobalExceptionHandler(
            ErrorResponseBuilder errorResponseBuilder
    ) {
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(
            ResourceNotFoundException exception
    ) {

        int status = HttpServletResponse.SC_NOT_FOUND;

        ErrorResponseDTO error = errorResponseBuilder.build(
                status,
                exception.getMessage(),
                exception.getArgs()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException exception
    ) {

        int status = HttpServletResponse.SC_UNAUTHORIZED;

        ErrorResponseDTO error = errorResponseBuilder.build(
                status,
                "error.auth.invalid-credentials"
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> errors = new LinkedHashMap<>();
        int status = HttpServletResponse.SC_BAD_REQUEST;

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        ValidationErrorResponseDTO errorResponse = new ValidationErrorResponseDTO(
                status,
                errors
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}