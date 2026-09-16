package com.gdu.wacdo.controllersTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdu.wacdo.config.TestConfiguration;
import com.gdu.wacdo.constants.CollaboratorRoles;
import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.factories.CollaboratorTestFactory;
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

        CollaboratorRequestDTO request = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

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

        Collaborator firstCollaborator = collaboratorRepository.save(
                CollaboratorTestFactory.createGenericCollaborator()
        );

        Collaborator secondtCollaborator = collaboratorRepository.save(
                CollaboratorTestFactory.createUpdatedCollaborator()
        );

        mockMvc.perform(get("/api/collaborators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(firstCollaborator.getId()))
                .andExpect(jsonPath("$[0].name").value("Dupont"))
                .andExpect(jsonPath("$[0].firstName").value("Jean"))
                .andExpect(jsonPath("$[0].email").value("jean.dupont@test.com"))
                .andExpect(jsonPath("$[0].firstHireDate").value("2000-10-10"))
                .andExpect(jsonPath("$[0].admin").value(false))

                .andExpect(jsonPath("$[1].id").value(secondtCollaborator.getId()))
                .andExpect(jsonPath("$[1].name").value("Bouchard"))
                .andExpect(jsonPath("$[1].firstName").value("Gérard"))
                .andExpect(jsonPath("$[1].email").value("gerard.bouchard@test.com"))
                .andExpect(jsonPath("$[1].firstHireDate").value("2010-10-10"))
                .andExpect(jsonPath("$[1].admin").value(true));
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
                        CollaboratorTestFactory.createGenericCollaborator()
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

    // UPDATE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void update_shouldUpdateCollaboratorInDatabase() throws Exception {

        // Assert that there's only one collaborator to be returned by findByEmail
        collaboratorRepository.deleteAll();

        // Create collaborator
        CollaboratorRequestDTO createRequest =
                CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

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
        CollaboratorRequestDTO updateRequest = CollaboratorTestFactory.createUpdatedCollaboratorRequestDTO();

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

    // DELETE
    @Test
    @WithMockUser(roles = CollaboratorRoles.ADMIN_ROLE)
    void delete_shouldRemoveCollaboratorFromDatabase() throws Exception {

        Collaborator collaborator = collaboratorRepository.save(
                CollaboratorTestFactory.createGenericCollaborator()
        );

        Long id = collaborator.getId();

        mockMvc.perform(
                        delete("/api/collaborators/{id}", id)
                )
                .andExpect(status().isOk());

        Optional<Collaborator> deletedCollaborator = collaboratorRepository.findById(id);

        assertTrue(deletedCollaborator.isEmpty());
    }
}