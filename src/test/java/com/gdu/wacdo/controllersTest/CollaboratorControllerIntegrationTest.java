package com.gdu.wacdo.controllersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.config.TestConfiguration;
import com.gdu.wacdo.constants.CollaboratorRoles;
import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.factories.CollaboratorDTOTestFactory;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestConfiguration.TestConfig.class)
class CollaboratorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        collaboratorRepository.deleteAll();
    }

    //CREATE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void create_shouldPersistCollaboratorInDatabase() throws Exception {

        CollaboratorRequestDTO request = CollaboratorDTOTestFactory.createGenericCollaboratorRequestDTO();

        mockMvc.perform(
                        post("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dupont"))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$.firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$.admin").value(false));

        Optional<Collaborator> collaborator = collaboratorRepository.findByEmail("jean.dupont@test.com");

        assertTrue(collaborator.isPresent());

        assertEquals("Dupont", collaborator.get().getName());
        assertEquals("Jean", collaborator.get().getFirstName());
        assertEquals("jean.dupont@test.com", collaborator.get().getEmail());
        assertEquals(
                LocalDate.of(2000, 10, 10),
                collaborator.get().getFirstHireDate()
        );
        assertFalse(collaborator.get().isAdmin());
        assertTrue(
                passwordEncoder.matches(
                        "password123",
                        collaborator.get().getPassword()
                )
        );
    }

    // FIND ALL
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findAll_shouldReturnCollaborators() throws Exception {

        collaboratorRepository.save(
                CollaboratorDTOTestFactory.createGenericCollaborator()
        );

        mockMvc.perform(
                        get("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Dupont"))
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[0].email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$[0].firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$[0].admin").value(false));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findAll_shouldReturnEmptyList_whenNoCollaboratorExists() throws Exception {

        mockMvc.perform(
                        get("/api/collaborators")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // FIND BY ID
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findById_shouldReturnCollaborator() throws Exception {


        Collaborator collaborator =
                collaboratorRepository.save(
                        CollaboratorDTOTestFactory.createGenericCollaborator()
                );
        Long id = collaborator.getId();

        mockMvc.perform(
                        get("/api/collaborators/{id}", id)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Dupont"))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$.firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void findById_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        Long id = 999L;

        // WHEN & THEN
        mockMvc.perform(
                        get("/api/collaborators/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: " + 999 + "."));
    }

    // UPDATE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldUpdateCollaboratorInDatabase() throws Exception {

        // Assert that there's only one collaborator to be returned by findByEmail
        collaboratorRepository.deleteAll();

        // Create collaborator
        CollaboratorRequestDTO createRequest =
                CollaboratorDTOTestFactory.createGenericCollaboratorRequestDTO();

        mockMvc.perform(
                        post("/api/collaborators")
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest))
                )
                .andExpect(status().isOk());

        Collaborator collaborator = collaboratorRepository.findByEmail("jean.dupont@test.com")
                .orElseThrow();
        Long id = collaborator.getId();

        // Update collaborator
        CollaboratorRequestDTO updateRequest = CollaboratorDTOTestFactory.createUpdatedCollaboratorRequestDTO();

        mockMvc.perform(
                        put("/api/collaborators/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Bouchard"))
                .andExpect(jsonPath("$.firstName").value("Gérard"))
                .andExpect(jsonPath("$.email").value("gerard.bouchard@test.com"))
                .andExpect(jsonPath("$.admin").value(true));

        // Database verification
        Collaborator updatedCollaborator = collaboratorRepository.findById(id)
                .orElseThrow();

        assertEquals("Bouchard", updatedCollaborator.getName());
        assertEquals("Gérard", updatedCollaborator.getFirstName());
        assertEquals("gerard.bouchard@test.com", updatedCollaborator.getEmail());
        assertEquals(LocalDate.of(2010, 10, 10),
                updatedCollaborator.getFirstHireDate());
        assertTrue(updatedCollaborator.isAdmin());
        assertTrue(
                passwordEncoder.matches(
                        "password456",
                        updatedCollaborator.getPassword()
                )
        );
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        Long id = 999L;

        CollaboratorRequestDTO request =
                CollaboratorDTOTestFactory.createUpdatedCollaboratorRequestDTO();

        mockMvc.perform(
                        put("/api/collaborators/{id}", id)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: " + id + "."));
    }

    // DELETE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void delete_shouldRemoveCollaboratorFromDatabase() throws Exception {

        Collaborator collaborator = collaboratorRepository.save(
                CollaboratorDTOTestFactory.createGenericCollaborator()
        );

        Long id = collaborator.getId();

        mockMvc.perform(
                        delete("/api/collaborators/{id}", id)
                )
                .andExpect(status().isOk());

        Optional<Collaborator> deletedCollaborator = collaboratorRepository.findById(id);

        assertTrue(deletedCollaborator.isEmpty());
    }

    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void delete_shouldReturn404_whenCollaboratorDoesNotExist() throws Exception {

        Long id = 999L;

        mockMvc.perform(
                        delete("/api/collaborators/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("No collaborator found with id: " + id + "."));
    }
}