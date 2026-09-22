package com.gdu.wacdo.servicesTest;

import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.exceptions.ResourceAlreadyExistsException;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.factories.CollaboratorTestFactory;
import com.gdu.wacdo.mappers.DTOMapper;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import com.gdu.wacdo.services.CollaboratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        Collaborator collaborator = CollaboratorTestFactory.createGenericCollaborator();
        Long id = collaborator.getId();

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
        assertThatThrownBy(
                () -> collaboratorService.findCollaboratorById(id)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // FIND ALL
    @Test
    void findAll_shouldReturnAllCollaborators() {

        // Arrange
        Collaborator collaborator1 = CollaboratorTestFactory.createGenericCollaborator();

        Collaborator collaborator2 = CollaboratorTestFactory.createUpdatedCollaborator();

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

        CollaboratorRequestDTO request = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

        Collaborator savedCollaborator = new Collaborator();

        CollaboratorResponseDTO response = new CollaboratorResponseDTO();

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(collaboratorRepository.save(any(Collaborator.class)))
                .thenReturn(savedCollaborator);

        when(dtoMapper.toCollaboratorResponseDTO(savedCollaborator))
                .thenReturn(response);

        CollaboratorResponseDTO result = collaboratorService.create(request);

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

    @Test
    void create_shouldThrowException_whenEmailAlreadyExists() {

        CollaboratorRequestDTO dto = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

        when(collaboratorRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> collaboratorService.create(dto)
        );

        verify(collaboratorRepository).existsByEmail(dto.getEmail());
        verify(collaboratorRepository, never()).save(any());
    }

    // UPDATE
    @Test
    void update_shouldUpdateCollaborator() {

        CollaboratorRequestDTO request = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

        Collaborator collaborator = new Collaborator();
        Long id = collaborator.getId();

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

    @Test
    void update_shouldThrowException_whenEmailAlreadyExistsForAnotherCollaborator() {

        Long id = 1L;
        CollaboratorRequestDTO dto = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

        when(collaboratorRepository.existsByEmailAndIdNot(
                dto.getEmail(),
                id
        )).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> collaboratorService.update(id, dto)
        );

        verify(collaboratorRepository)
                .existsByEmailAndIdNot(dto.getEmail(), id);

        verify(collaboratorRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void update_shouldNotEncodePassword_whenPasswordIsNullOrBlank(String password) {

        Collaborator collaborator = CollaboratorTestFactory.createGenericCollaborator();
        Long id = collaborator.getId();
        String originalPassword = collaborator.getPassword();

        CollaboratorRequestDTO dto = CollaboratorTestFactory.createGenericCollaboratorRequestDTO();

        dto.setPassword(password);

        when(collaboratorRepository.findById(id))
                .thenReturn(Optional.of(collaborator));

        when(collaboratorRepository.existsByEmailAndIdNot(
                dto.getEmail(),
                id
        )).thenReturn(false);

        when(collaboratorRepository.save(any(Collaborator.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        collaboratorService.update(id, dto);

        verify(passwordEncoder, never()).encode(any());

        assertEquals(originalPassword, collaborator.getPassword());
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