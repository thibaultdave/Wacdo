package com.gdu.wacdo.servicesTest;

import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.mappers.DTOMapper;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import com.gdu.wacdo.services.CollaboratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

    @Mock
    private CollaboratorRepository collaboratorRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private DTOMapper dtoMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CollaboratorService collaboratorService;

    @BeforeEach
    void setUp() {
        collaboratorService = new CollaboratorService(
                collaboratorRepository,
                modelMapper,
                dtoMapper,
                passwordEncoder
        );
    }

    // FIND COLLABORATOR BY ID
    @Test
    void findCollaboratorById_shouldReturnCollaborator_whenIdExists() {

        // Arrange
        Long id = 1L;

        Collaborator collaborator = new Collaborator();
        collaborator.setId(id);

        when(collaboratorRepository.findById(id))
                .thenReturn(Optional.of(collaborator));

        // Act
        Collaborator result = collaboratorService.findCollaboratorById(id);

        // Assert
        assertThat(result).isSameAs(collaborator);
    }

    @Test
    void findCollaboratorById_shouldThrowException_whenIdDoesNotExist() {

        // Arrange
        Long id = 999L;

        when(collaboratorRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> collaboratorService.findCollaboratorById(id)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // FIND ALL
    @Test
    void findAll_shouldReturnAllCollaborators() {

        // Arrange
        Collaborator collaborator1 = new Collaborator();
        collaborator1.setId(1L);

        Collaborator collaborator2 = new Collaborator();
        collaborator2.setId(2L);

        CollaboratorResponseDTO response1 = new CollaboratorResponseDTO();
        CollaboratorResponseDTO response2 = new CollaboratorResponseDTO();

        when(collaboratorRepository.findAll())
                .thenReturn(List.of(collaborator1, collaborator2));

        when(dtoMapper.toCollaboratorResponseDTO(collaborator1))
                .thenReturn(response1);

        when(dtoMapper.toCollaboratorResponseDTO(collaborator2))
                .thenReturn(response2);

        // Act
        List<CollaboratorResponseDTO> result = collaboratorService.findAll();

        // Assert
        assertThat(result)
                .containsExactly(response1, response2);
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoCollaboratorExists() {

        when(collaboratorRepository.findAll())
                .thenReturn(List.of());

        List<CollaboratorResponseDTO> result = collaboratorService.findAll();

        assertThat(result)
                .isEmpty();
    }

    // CREATE
    @Test
    void create_shouldCreateCollaborator() {

        CollaboratorRequestDTO request = new CollaboratorRequestDTO();

        request.setName("Doe");
        request.setFirstName("John");
        request.setEmail("john.doe@test.com");
        request.setPassword("password123");

        Collaborator savedCollaborator = new Collaborator();
        savedCollaborator.setId(1L);

        CollaboratorResponseDTO response = new CollaboratorResponseDTO();

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(collaboratorRepository.save(any(Collaborator.class)))
                .thenReturn(savedCollaborator);

        when(dtoMapper.toCollaboratorResponseDTO(savedCollaborator))
                .thenReturn(response);

        CollaboratorResponseDTO result =
                collaboratorService.create(request);

        assertThat(result)
                .isSameAs(response);

        verify(modelMapper)
                .map(eq(request), any(Collaborator.class));

        verify(passwordEncoder)
                .encode(request.getPassword());

        verify(collaboratorRepository)
                .save(any(Collaborator.class));

        verify(dtoMapper)
                .toCollaboratorResponseDTO(savedCollaborator);
    }

    // UPDATE
    @Test
    void update_shouldUpdateCollaborator() {

        Long id = 1L;

        CollaboratorRequestDTO request = new CollaboratorRequestDTO();
        request.setName("Doe");
        request.setFirstName("John");
        request.setEmail("john.doe@test.com");
        request.setPassword("newPassword123");

        Collaborator collaborator = new Collaborator();
        collaborator.setId(id);

        CollaboratorResponseDTO response = new CollaboratorResponseDTO();

        when(collaboratorRepository.findById(id))
                .thenReturn(Optional.of(collaborator));

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(collaboratorRepository.save(collaborator))
                .thenReturn(collaborator);

        when(dtoMapper.toCollaboratorResponseDTO(collaborator))
                .thenReturn(response);

        CollaboratorResponseDTO result = collaboratorService.update(id, request);

        assertThat(result)
                .isSameAs(response);

        verify(collaboratorRepository)
                .findById(id);

        verify(modelMapper)
                .map(eq(request), eq(collaborator));

        verify(passwordEncoder)
                .encode(request.getPassword());

        verify(collaboratorRepository)
                .save(collaborator);

        verify(dtoMapper)
                .toCollaboratorResponseDTO(collaborator);
    }

    // DELETE
    @Test
    void deleteById_shouldDeleteCollaborator_whenIdExists() {

        Long id = 1L;

        when(collaboratorRepository.existsById(id))
                .thenReturn(true);

        collaboratorService.deleteById(id);

        verify(collaboratorRepository)
                .existsById(id);

        verify(collaboratorRepository)
                .deleteById(id);
    }

    @Test
    void deleteById_shouldThrowException_whenIdDoesNotExist() {

        Long id = 999L;

        when(collaboratorRepository.existsById(id))
                .thenReturn(false);

        assertThatThrownBy(
                () -> collaboratorService.deleteById(id)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(collaboratorRepository)
                .existsById(id);

        verify(collaboratorRepository, never())
                .deleteById(id);
    }
}