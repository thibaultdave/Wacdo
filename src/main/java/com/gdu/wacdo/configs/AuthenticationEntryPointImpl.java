package com.gdu.wacdo.configs;

import com.gdu.wacdo.constants.ExceptionMessages;
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

        response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessages.WRONG_EMAIL_OR_PASSWORD
        );
    }
}