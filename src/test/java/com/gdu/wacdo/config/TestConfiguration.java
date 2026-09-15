package com.gdu.wacdo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.constants.CollaboratorRoles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class TestConfiguration {

    @org.springframework.boot.test.context.TestConfiguration
    public static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper()
                    .findAndRegisterModules();
        }
    }

    @org.springframework.boot.test.context.TestConfiguration
    public static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().hasRole(CollaboratorRoles.ADMIN_ROLE)
                    )
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint(
                                    (request, response, authException) ->
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                            )
                    );

            return http.build();
        }
    }
}