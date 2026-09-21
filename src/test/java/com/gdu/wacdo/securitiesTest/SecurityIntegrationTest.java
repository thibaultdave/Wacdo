package com.gdu.wacdo.securitiesTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.config.TestConfiguration;
import com.gdu.wacdo.dto.LoginRequestDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.factories.CollaboratorTestFactory;
import com.gdu.wacdo.factories.LoginTestFactory;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import com.gdu.wacdo.securities.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfiguration.TestConfig.class)
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void cleanDatabase() {
        collaboratorRepository.deleteAll();
    }

    // ENDPOINTS
    @Test
    void protectedEndpoint_shouldReturn401_whenNoTokenIsProvided() throws Exception {

        mockMvc.perform(get("/api/collaborators"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn401_whenInvalidToken() throws Exception {

        mockMvc.perform(
                        get("/api/collaborators")
                                .header("Authorization", "Bearer invalid-token")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn403_whenValidTokenAndNotAdmin() throws Exception {

        // Collaborator who !isAdmin
        Collaborator collaborator =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createGenericCollaborator()
                );

        String token = jwtService.generateToken(
                collaborator.getEmail()
        );

        mockMvc.perform(
                        get("/api/collaborators")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_shouldReturn200_whenValidTokenAndAdmin() throws Exception {

        // Collaborator who isAdmin
        Collaborator collaborator =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createUpdatedCollaborator()
                );

        String token = jwtService.generateToken(
                collaborator.getEmail()
        );

        mockMvc.perform(
                        get("/api/collaborators")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }

    // LOGIN
    @Test
    void login_shouldReturn200AndToken_whenValidCredentials() throws Exception {

        // Collaborator who isAdmin
        Collaborator collaborator = CollaboratorTestFactory.createUpdatedCollaborator();
        collaborator.setPassword(
                passwordEncoder.encode(collaborator.getPassword())
        );
        collaboratorRepository.save(collaborator);

        LoginRequestDTO loginRequest = LoginTestFactory.createAdminLoginRequestDTO();

        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        assertNotNull(json.get("token"));
        assertFalse(json.get("token").asText().isBlank());
    }

    @Test
    void login_shouldReturnTokenThatCanAccessProtectedEndpoint() throws Exception {

        // Collaborator who isAdmin
        Collaborator collaborator = CollaboratorTestFactory.createUpdatedCollaborator();
        collaborator.setPassword(
                passwordEncoder.encode(collaborator.getPassword())
        );
        collaboratorRepository.save(collaborator);

        LoginRequestDTO loginRequest = LoginTestFactory.createAdminLoginRequestDTO();

        String response = mockMvc.perform(
                        post("/auth/login")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        String token = json.get("token").asText();

        assertFalse(token.isBlank());

        mockMvc.perform(
                        get("/api/collaborators")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }
}