package com.gdu.wacdo.controllersTest;

import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.configs.AuthenticationEntryPointImpl;
import com.gdu.wacdo.configs.SecurityConfig;
import com.gdu.wacdo.constants.CollaboratorRoles;
import com.gdu.wacdo.controllers.CollaboratorController;
import com.gdu.wacdo.securities.AccessDeniedHandlerImpl;
import com.gdu.wacdo.securities.JwtAuthenticationFilter;
import com.gdu.wacdo.securities.JwtService;
import com.gdu.wacdo.services.CollaboratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollaboratorController.class)
class CollaboratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollaboratorService collaboratorService;

    @MockitoBean
    private ErrorResponseBuilder errorResponseBuilder;

    @MockitoBean
    private JwtService jwtService;

    @TestConfiguration
    static class TestSecurityConfig {

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

    // FIND ALL
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findAll_shouldReturn200() throws Exception {

        when(collaboratorService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/collaborators"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.USER_ROLE)
    void findAll_shouldReturn403_whenUserIsNotAdmin() throws Exception {

        mockMvc.perform(get("/api/collaborators"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAll_shouldReturn401_whenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/collaborators"))
                .andExpect(status().isUnauthorized());
    }
}