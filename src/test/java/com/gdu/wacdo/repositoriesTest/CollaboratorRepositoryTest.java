package com.gdu.wacdo.repositoriesTest;

import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.factories.CollaboratorTestFactory;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CollaboratorRepositoryTest {

    @Autowired
    private CollaboratorRepository collaboratorRepository;

    // FIND BY EMAIL
    @Test
    void findByEmail_shouldReturnCollaborator_whenEmailExists() {

        Collaborator collaborator =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createGenericCollaborator()
                );

        Optional<Collaborator> result =
                collaboratorRepository.findByEmail(collaborator.getEmail());

        assertTrue(result.isPresent());
        assertEquals("Jean", result.get().getFirstName());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {

        Optional<Collaborator> result = collaboratorRepository.findByEmail("inexistant@test.com");

        assertTrue(result.isEmpty());
    }

    // EXISTS BY EMAIL
    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {

        collaboratorRepository.save(
                CollaboratorTestFactory.createGenericCollaborator()
        );

        assertTrue(
                collaboratorRepository.existsByEmail("jean.dupont@test.com")
        );
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {

        assertFalse(
                collaboratorRepository.existsByEmail("inexistant@test.com")
        );
    }

    // EXISTS BY EMAIL AND ID NOT
    @Test
    void existsByEmailAndIdNot_shouldReturnFalse_whenEmailBelongsToSameCollaborator() {

        Collaborator collaborator =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createGenericCollaborator()
                );

        assertFalse(
                collaboratorRepository.existsByEmailAndIdNot(
                        collaborator.getEmail(),
                        collaborator.getId()
                )
        );
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrue_whenEmailBelongsToAnotherCollaborator() {

        Collaborator jean =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createGenericCollaborator()
                );

        Collaborator gerard =
                collaboratorRepository.save(
                        CollaboratorTestFactory.createUpdatedCollaborator()
                );

        assertTrue(
                collaboratorRepository.existsByEmailAndIdNot(
                        gerard.getEmail(),
                        jean.getId()
                )
        );
    }
}