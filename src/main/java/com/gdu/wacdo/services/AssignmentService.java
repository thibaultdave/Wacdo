package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.AssignmentResponseDTO;
import com.gdu.wacdo.dto.AssignmentRequestDTO;
import com.gdu.wacdo.entities.Assignment;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.entities.Job;
import com.gdu.wacdo.entities.Restaurant;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.mappers.DTOMapper;
import com.gdu.wacdo.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CollaboratorService collaboratorService;
    private final RestaurantService restaurantService;
    private final JobService jobService;
    private final DTOMapper dtoMapper;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             CollaboratorService collaboratorService,
                             RestaurantService restaurantService,
                             JobService jobService,
                             DTOMapper dtoMapper
    ) {
        this.assignmentRepository = assignmentRepository;
        this.collaboratorService = collaboratorService;
        this.restaurantService = restaurantService;
        this.jobService = jobService;
        this.dtoMapper = dtoMapper;
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

        Assignment createdAssignment = assignmentRepository.save(toEntity(dto));

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
        return dtoMapper.toAssignmentResponseDTO(assignment);
    }

    private Assignment toEntity(AssignmentRequestDTO dto) {
        Assignment assignment = new Assignment();

        return setAssignmentFromRequest(assignment, dto);
    }

    private Assignment setAssignmentFromRequest(Assignment assignment, AssignmentRequestDTO dto) {
        Collaborator collaborator = collaboratorService.findCollaboratorById(dto.getCollaboratorId());
        Restaurant restaurant = restaurantService.findRestaurantById(dto.getRestaurantId());
        Job job = jobService.findJobById(dto.getJobId());

        assignment.setCollaborator(collaborator);
        assignment.setRestaurant(restaurant);
        assignment.setJob(job);
        assignment.setStartDate(dto.getStartDate());
        assignment.setEndDate(dto.getEndDate());

        return assignment;
    }
}