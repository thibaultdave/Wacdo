package com.gdu.wacdo.securities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ErrorResponseBuilder errorResponseBuilder;

    public AccessDeniedHandlerImpl(
            ErrorResponseBuilder errorResponseBuilder
    ) {
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        int status = HttpServletResponse.SC_FORBIDDEN;

        ErrorResponseDTO error = errorResponseBuilder.build(
                status,
                "error.auth.access-denied"
        );
        response.setStatus(status);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}