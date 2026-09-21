package com.gdu.wacdo.securitiesTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.config.TestConfiguration;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.factories.CollaboratorTestFactory;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}