package com.gdu.wacdo.controllersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.builders.ErrorResponseBuilder;
import com.gdu.wacdo.config.TestConfiguration;
import com.gdu.wacdo.constants.CollaboratorRoles;
import com.gdu.wacdo.controllers.CollaboratorController;
import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.ErrorResponseDTO;
import com.gdu.wacdo.exceptions.ResourceAlreadyExistsException;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.securities.JwtService;
import com.gdu.wacdo.services.CollaboratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static com.gdu.wacdo.factories.CollaboratorTestFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(CollaboratorController.class)
@ActiveProfiles("test")
@Import({TestConfiguration.TestConfig.class, TestConfiguration.TestSecurityConfig.class})
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

    // ENDPOINTS CHECK
    private static Stream<Arguments> protectedEndpoints() {
        return Stream.of(
                Arguments.of("GET", "/api/collaborators"),
                Arguments.of("GET", "/api/collaborators/1"),
                Arguments.of("POST", "/api/collaborators"),
                Arguments.of("PUT", "/api/collaborators/1"),
                Arguments.of("DELETE", "/api/collaborators/1")
        );
    }

    @ParameterizedTest
    @MethodSource("protectedEndpoints")
    @WithMockUser(roles = CollaboratorRoles.USER_ROLE)
    void protectedEndpoints_shouldReturn403_whenUserIsNotAdmin(
            String method,
            String endpoint
    ) throws Exception {

        mockMvc.perform(
                        request(
                                HttpMethod.valueOf(method),
                                endpoint
                        )
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isForbidden());
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

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void create_shouldReturn409_whenEmailAlreadyExists() throws Exception {

        int status = HttpServletResponse.SC_CONFLICT;

        when(collaboratorService.create(any(CollaboratorRequestDTO.class)))
                .thenThrow(
                        new ResourceAlreadyExistsException(
                                "error.collaborator.email-already-exists",
                                "jean.dupont@test.com"
                        )
                );

        when(errorResponseBuilder.build(
                eq(status),
                eq("error.collaborator.email-already-exists"),
                eq("jean.dupont@test.com")
        )).thenReturn(
                new ErrorResponseDTO(
                        status,
                        "A collaborator already exists with email: jean.dupont@test.com."
                )
        );

        mockMvc.perform(
                        post("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        createGenericCollaboratorRequestDTO()
                                ))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("A collaborator already exists with email: jean.dupont@test.com."));
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
                        "No collaborator found with id: " + id + "."
                )
        );

        mockMvc.perform(put("/api/collaborators/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                createGenericCollaboratorRequestDTO()
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: " + id + "."));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldReturn409_whenEmailAlreadyExists() throws Exception {

        int status = HttpServletResponse.SC_CONFLICT;
        long id = 1L;
        String email = "jean.dupont@test.com";

        when(collaboratorService.update(
                eq(id),
                any(CollaboratorRequestDTO.class)
        )).thenThrow(
                new ResourceAlreadyExistsException(
                        "error.collaborator.email-already-exists",
                        email
                )
        );

        when(errorResponseBuilder.build(
                eq(status),
                eq("error.collaborator.email-already-exists"),
                eq(email)
        )).thenReturn(
                new ErrorResponseDTO(
                        status,
                        "A collaborator already exists with email: " + email +"."
                )
        );

        mockMvc.perform(
                        put("/api/collaborators/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        createUpdatedCollaboratorRequestDTO()
                                ))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.message")
                        .value("A collaborator already exists with email: " + email +"."));
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
}