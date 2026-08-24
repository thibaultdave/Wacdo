package com.gdu.wacdo.mappers;

import com.gdu.wacdo.dto.*;
import com.gdu.wacdo.entities.Assignment;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.entities.Restaurant;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    private final ModelMapper modelMapper;

    public DTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // COLLABORATOR

    public CollaboratorResponseDTO toCollaboratorResponseDTO(Collaborator collaborator) {

        CollaboratorResponseDTO dto =  modelMapper.map(collaborator, CollaboratorResponseDTO.class);
        dto.setAssignments(
                collaborator.getAssignments()
                        .stream()
                        .map(this::toAssignmentResponseDTO)
                        .toList()
        );

        return dto;
    }

    public CollaboratorSummaryDTO toCollaboratorSummaryDTO(Collaborator collaborator) {
        return modelMapper.map(collaborator, CollaboratorSummaryDTO.class);
    }

    // RESTAURANT

    public RestaurantResponseDTO toRestaurantResponseDTO(Restaurant restaurant) {

        RestaurantResponseDTO dto = modelMapper.map(restaurant, RestaurantResponseDTO.class);
        dto.setAssignments(
                restaurant.getAssignments()
                        .stream()
                        .map(this::toAssignmentResponseDTO)
                        .toList()
        );

        return dto;
    }

    public RestaurantSummaryDTO toRestaurantSummaryDTO(Restaurant restaurant) {
        return modelMapper.map(restaurant, RestaurantSummaryDTO.class);
    }

    // ASSIGNMENT

    public AssignmentResponseDTO toAssignmentResponseDTO(Assignment assignment) {

        AssignmentResponseDTO dto = modelMapper.map(assignment, AssignmentResponseDTO.class);
        dto.setCollaborator(
                toCollaboratorSummaryDTO(assignment.getCollaborator())
        );
        dto.setRestaurant(
                toRestaurantSummaryDTO(assignment.getRestaurant())
        );

        return dto;
    }
}