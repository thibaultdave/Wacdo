package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.RestaurantRequestDTO;
import com.gdu.wacdo.dto.RestaurantResponseDTO;
import com.gdu.wacdo.entities.Restaurant;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.repositories.RestaurantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<RestaurantResponseDTO> findAll() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Restaurant findRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.RESTAURANT_NOT_FOUND, id
                ));
    }

    public RestaurantResponseDTO findById(Long id) {
        return toResponseDTO(findRestaurantById(id));
    }

    public RestaurantResponseDTO create(RestaurantRequestDTO dto) {

        Restaurant createdRestaurant = restaurantRepository.save(toEntity(dto));

        return toResponseDTO(createdRestaurant);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO dto) {

        Restaurant restaurant = findRestaurantById(id);
        Restaurant updatedRestaurant = restaurantRepository.save(
                setRestaurantFromRequest(restaurant, dto)
        );

        return toResponseDTO(updatedRestaurant);
    }

    public void deleteById(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ExceptionMessages.RESTAURANT_NOT_FOUND, id
            );
        }

        restaurantRepository.deleteById(id);
    }

    // DTO METHODS

    private RestaurantResponseDTO toResponseDTO(Restaurant restaurant) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(restaurant, RestaurantResponseDTO.class);
    }

    private Restaurant toEntity(RestaurantRequestDTO dto) {
        Restaurant restaurant = new Restaurant();

        return setRestaurantFromRequest(restaurant, dto);
    }

    private Restaurant setRestaurantFromRequest(Restaurant restaurant, RestaurantRequestDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.map(dto, restaurant);

        return restaurant;
    }
}