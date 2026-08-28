package com.gdu.wacdo.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpServletResponse.SC_UNAUTHORIZED,
                ExceptionMessages.MUST_LOG_TO_ACCESS
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}