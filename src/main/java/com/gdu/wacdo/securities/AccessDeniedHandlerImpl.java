package com.gdu.wacdo.securities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        ErrorResponseDTO error = new ErrorResponseDTO(
                HttpServletResponse.SC_FORBIDDEN,
                ExceptionMessages.NOT_ENOUGH_PRIVILEGE
        );
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}