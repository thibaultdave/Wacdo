package com.gdu.wacdo.controllersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.constants.CollaboratorRoles;
import com.gdu.wacdo.controllers.CollaboratorController;
import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.securities.JwtService;
import com.gdu.wacdo.services.CollaboratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.gdu.wacdo.factories.CollaboratorDTOTestFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(CollaboratorController.class)
class CollaboratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CollaboratorService collaboratorService;

    @MockitoBean
    private ErrorResponseBuilder errorResponseBuilder;

    @MockitoBean
    private JwtService jwtService;

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

    //TODO Add response test ?

    // FIND BY ID
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findById_shouldReturn200_whenCollaboratorExists() throws Exception {

        when(collaboratorService.findById(1L))
                .thenReturn(createGenericCollaboratorResponseDTO());

        mockMvc.perform(get("/api/collaborators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dupont"))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$.firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findById_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        int status = HttpServletResponse.SC_NOT_FOUND;
        long id = 99L;

        when(collaboratorService.findById(id))
                .thenThrow(new ResourceNotFoundException(
                        "error.collaborator.not-found",
                        id
                ));

        when(errorResponseBuilder.build(
                eq(status),
                eq("error.collaborator.not-found"),
                eq(id)
        )).thenReturn(
                new ErrorResponseDTO(
                        status,
                        "No collaborator found with id: 99."
                )
        );

        mockMvc.perform(get("/api/collaborators/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: 99."));
    }

    // CREATE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void create_shouldReturn200_whenCollaboratorIsValid() throws Exception {

        when(collaboratorService.create(any(CollaboratorRequestDTO.class)))
                .thenReturn(createGenericCollaboratorResponseDTO());

        mockMvc.perform(
                        post("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        createGenericCollaboratorRequestDTO()
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dupont"))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$.firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void create_shouldReturn400_whenCollaboratorIsInvalid() throws Exception {

        mockMvc.perform(
                        post("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        createFailedCollaboratorRequestDTO()
                                ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("{validation.collaborator.name.not-blank}"))
                .andExpect(jsonPath("$.errors.firstName").value("{validation.collaborator.first-name.not-blank}"))
                .andExpect(jsonPath("$.errors.email").value("{validation.collaborator.email.invalid}"))
                .andExpect(jsonPath("$.errors.firstHireDate").value("{validation.collaborator.first-hire-date.invalid}"))
                .andExpect(jsonPath("$.errors.password").value("{validation.collaborator.password.size}"));
    }

    // UPDATE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldReturn200_whenCollaboratorIsValid() throws Exception {

        when(collaboratorService.update(1L, createUpdatedCollaboratorRequestDTO()))
                .thenReturn(
                        createUpdatedCollaboratorResponseDTO()
                );

        mockMvc.perform(
                put("/api/collaborators/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                createUpdatedCollaboratorRequestDTO()
                        ))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Bouchard"))
                .andExpect(jsonPath("$.firstName").value("Gérard"))
                .andExpect(jsonPath("$.email").value("gerard.bouchard@test.com"))
                .andExpect(jsonPath("$.firstHireDate").value("2010-10-10"))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldReturn400_whenCollaboratorIsInvalid() throws Exception {

        mockMvc.perform(
                        put("/api/collaborators/1")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        createFailedCollaboratorRequestDTO()
                                ))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("{validation.collaborator.name.not-blank}"))
                .andExpect(jsonPath("$.errors.firstName").value("{validation.collaborator.first-name.not-blank}"))
                .andExpect(jsonPath("$.errors.email").value("{validation.collaborator.email.invalid}"))
                .andExpect(jsonPath("$.errors.firstHireDate").value("{validation.collaborator.first-hire-date.invalid}"))
                .andExpect(jsonPath("$.errors.password").value("{validation.collaborator.password.size}"));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        int status = HttpServletResponse.SC_NOT_FOUND;
        long id = 99L;

        when(collaboratorService.update(
                eq(id),
                any(CollaboratorRequestDTO.class)
        )).thenThrow(
                new ResourceNotFoundException(
                        "error.collaborator.not-found",
                        id
                ));

        when(errorResponseBuilder.build(
                eq(status),
                eq("error.collaborator.not-found"),
                eq(id)
        )).thenReturn(
                new ErrorResponseDTO(
                        status,
                        "No collaborator found with id: 99."
                )
        );

        mockMvc.perform(put("/api/collaborators/99")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                createGenericCollaboratorRequestDTO()
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: 99."));
    }

    // DELETE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void delete_shouldReturn200_whenCollaboratorExists() throws Exception {

        long id = 1L;

        doNothing().when(collaboratorService).deleteById(id);

        mockMvc.perform(
                        delete("/api/collaborators/" + id)
                )
                .andExpect(status().isOk());

        verify(collaboratorService).deleteById(id);
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void delete_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        long id = 99L;
        int status = HttpServletResponse.SC_NOT_FOUND;

        doThrow(
                new ResourceNotFoundException(
                        "error.collaborator.not-found",
                        id
                )
        ).when(collaboratorService).deleteById(id);

        when(errorResponseBuilder.build(
                eq(status),
                eq("error.collaborator.not-found"),
                eq(id)
        )).thenReturn(
                new ErrorResponseDTO(
                        status,
                        "No collaborator found with id: 99."
                )
        );

        mockMvc.perform(delete("/api/collaborators/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: 99."));
    }

    // CONFIG METHODS
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

    @TestConfiguration
    static class TestConfig {

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper()
                    .findAndRegisterModules();
        }
    }


}