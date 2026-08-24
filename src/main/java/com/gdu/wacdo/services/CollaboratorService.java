package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.mappers.DTOMapper;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final ModelMapper modelMapper;
    private final DTOMapper dtoMapper;

    public CollaboratorService(
            CollaboratorRepository collaboratorRepository,
            ModelMapper modelMapper,
            DTOMapper dtoMapper) {
        this.collaboratorRepository = collaboratorRepository;
        this.modelMapper = modelMapper;
        this.dtoMapper = dtoMapper;
    }

    public List<CollaboratorResponseDTO> findAll() {
        return collaboratorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Collaborator findCollaboratorById(Long id) {
        return collaboratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.COLLABORATOR_NOT_FOUND, id
                ));
    }

    public CollaboratorResponseDTO findById(Long id) {
        return toResponseDTO(findCollaboratorById(id));
    }

    public CollaboratorResponseDTO create(CollaboratorRequestDTO dto) {

        Collaborator createdCollaborator = collaboratorRepository.save(toEntity(dto));

        return toResponseDTO(createdCollaborator);
    }

    public CollaboratorResponseDTO update(Long id, CollaboratorRequestDTO dto) {

        Collaborator collaborator = findCollaboratorById(id);
        Collaborator updatedCollaborator = collaboratorRepository.save(
                setCollaboratorFromRequest(collaborator, dto)
        );

        return toResponseDTO(updatedCollaborator);
    }

    public void deleteById(Long id) {
        if (!collaboratorRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ExceptionMessages.COLLABORATOR_NOT_FOUND, id
            );
        }

        collaboratorRepository.deleteById(id);
    }

    // DTO METHODS

    private CollaboratorResponseDTO toResponseDTO(Collaborator collaborator) {
        return dtoMapper.toCollaboratorResponseDTO(collaborator);
    }

    private Collaborator toEntity(CollaboratorRequestDTO dto) {
        Collaborator collaborator = new Collaborator();

        return setCollaboratorFromRequest(collaborator, dto);
    }

    private Collaborator setCollaboratorFromRequest(Collaborator collaborator, CollaboratorRequestDTO dto) {
        modelMapper.map(dto, collaborator);

        return collaborator;
    }
}