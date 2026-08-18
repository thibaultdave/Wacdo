package com.gdu.wacdo.controllers;

import com.gdu.wacdo.dto.RestaurantRequestDTO;
import com.gdu.wacdo.dto.RestaurantResponseDTO;
import com.gdu.wacdo.services.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<RestaurantResponseDTO> findAll() {
        return restaurantService.findAll();
    }

    @GetMapping("/{id}")
    public RestaurantResponseDTO findById(@PathVariable Long id) {
        return restaurantService.findById(id);
    }

    @PostMapping
    public RestaurantResponseDTO create(@RequestBody RestaurantRequestDTO dto) {
        return restaurantService.create(dto);
    }

    @PutMapping("/{id}")
    public RestaurantResponseDTO update(@PathVariable Long id, @RequestBody RestaurantRequestDTO dto) {
        return restaurantService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        restaurantService.deleteById(id);
    }
}