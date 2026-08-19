package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.AssignmentResponseDTO;
import com.gdu.wacdo.dto.AssignmentRequestDTO;
import com.gdu.wacdo.entities.Assignment;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.entities.Job;
import com.gdu.wacdo.entities.Restaurant;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.repositories.AssignmentRepository;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import com.gdu.wacdo.repositories.JobRepository;
import com.gdu.wacdo.repositories.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final RestaurantRepository restaurantRepository;
    private final JobRepository jobRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             CollaboratorRepository collaboratorRepository,
                             RestaurantRepository restaurantRepository,
                             JobRepository jobRepository
    ) {
        this.assignmentRepository = assignmentRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.restaurantRepository = restaurantRepository;
        this.jobRepository = jobRepository;
    }

    public List<AssignmentResponseDTO> findAll() {
        return assignmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Assignment findAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.ASSIGNMENT_NOT_FOUND, id
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
            throw new ResourceNotFoundException(
                    ExceptionMessages.ASSIGNMENT_NOT_FOUND, id
            );
        }

        assignmentRepository.deleteById(id);
    }

    // DTO METHODS

    private AssignmentResponseDTO toResponseDTO(Assignment assignment) {
        ModelMapper modelMapper = new ModelMapper();

        AssignmentResponseDTO responseDTO = modelMapper.map(assignment, AssignmentResponseDTO.class);
        responseDTO.setCollaboratorId(assignment.getCollaborator().getId());
        responseDTO.setRestaurantId(assignment.getRestaurant().getId());
        responseDTO.setJobId(assignment.getJob().getId());

        return responseDTO;
    }

    private Assignment toEntity(AssignmentRequestDTO dto) {
        Assignment assignment = new Assignment();

        return setAssignmentFromRequest(assignment, dto);
    }

    private Assignment setAssignmentFromRequest(Assignment assignment, AssignmentRequestDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        Collaborator collaborator = collaboratorRepository
                .findById(dto.getCollaboratorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.COLLABORATOR_NOT_FOUND, dto.getCollaboratorId()
                ));
        Restaurant restaurant = restaurantRepository
                .findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.RESTAURANT_NOT_FOUND, dto.getRestaurantId()
                ));
        Job job = jobRepository
                .findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.JOB_NOT_FOUND, dto.getJobId()
                ));

        assignment = modelMapper.map(dto, Assignment.class);
        assignment.setCollaborator(collaborator);
        assignment.setRestaurant(restaurant);
        assignment.setJob(job);

        return assignment;
    }
}