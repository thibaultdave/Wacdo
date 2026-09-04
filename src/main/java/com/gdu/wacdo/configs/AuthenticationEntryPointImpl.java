package com.gdu.wacdo.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    private final ErrorResponseBuilder errorResponseBuilder;

    public AuthenticationEntryPointImpl(
            ErrorResponseBuilder errorResponseBuilder
    ) {
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        int status = HttpServletResponse.SC_UNAUTHORIZED;

        ErrorResponseDTO error = errorResponseBuilder.build(
                status,
                 "error.auth.authentication-required"
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