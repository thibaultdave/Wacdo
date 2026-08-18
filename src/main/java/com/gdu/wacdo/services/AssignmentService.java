package com.gdu.wacdo.services;

import com.gdu.wacdo.dto.AssignmentResponseDTO;
import com.gdu.wacdo.dto.AssignmentRequestDTO;
import com.gdu.wacdo.entities.Assignment;
import com.gdu.wacdo.repositories.AssignmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<AssignmentResponseDTO> findAll() {
        return assignmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Affectation introuvable avec l'id : " + id
                ));
    }

    public AssignmentResponseDTO findById(Long id) {
        return toResponseDTO(findAssignmentById(id));
    }

    public AssignmentResponseDTO create(AssignmentRequestDTO dto) {

        Assignment assignment = toEntity(dto);
        Assignment createdAssignment = assignmentRepository.save(assignment);

        return toResponseDTO(createdAssignment);
    }

    public AssignmentResponseDTO update(Long id, AssignmentRequestDTO dto) {

        Assignment assignment = findAssignmentById(id);
        Assignment updatedAssignment = assignmentRepository.save(
                setAssignmentFromRequest(assignment, dto)
        );

        return toResponseDTO(updatedAssignment);
    }

    public void deleteById(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Affectation introuvable avec l'id : " + id
            );
        }

        assignmentRepository.deleteById(id);
    }

    // DTO METHODS

    private AssignmentResponseDTO toResponseDTO(Assignment assignment) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(assignment, AssignmentResponseDTO.class);
    }

    private Assignment toEntity(AssignmentRequestDTO dto) {
        Assignment assignment = new Assignment();

        return setAssignmentFromRequest(assignment, dto);
    }

    private Assignment setAssignmentFromRequest(Assignment assignment, AssignmentRequestDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(dto, Assignment.class);
    }
}