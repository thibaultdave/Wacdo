package com.gdu.wacdo.configs;

import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.dto.RestaurantResponseDTO;
import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.entities.Restaurant;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(
                Collaborator.class,
                CollaboratorResponseDTO.class
        ).addMappings(mapper ->
                mapper.skip(CollaboratorResponseDTO::setAssignments)
        );

        modelMapper.typeMap(
                Restaurant.class,
                RestaurantResponseDTO.class
        ).addMappings(mapper ->
                mapper.skip(RestaurantResponseDTO::setAssignments)
        );

        return modelMapper;
    }
}