package com.gdu.wacdo.controllers;

import com.gdu.wacdo.dto.CollaboratorRequestDTO;
import com.gdu.wacdo.dto.CollaboratorResponseDTO;
import com.gdu.wacdo.services.CollaboratorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collaborators")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @GetMapping
    public List<CollaboratorResponseDTO> findAll() {
        return collaboratorService.findAll();
    }

    @GetMapping("/{id}")
    public CollaboratorResponseDTO findById(@PathVariable Long id) {
        return collaboratorService.findById(id);
    }

    @PostMapping
    public CollaboratorResponseDTO create(@RequestBody CollaboratorRequestDTO dto) {
        return collaboratorService.create(dto);
    }

    @PutMapping("/{id}")
    public CollaboratorResponseDTO update(@PathVariable Long id, @RequestBody CollaboratorRequestDTO dto) {
        return collaboratorService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        collaboratorService.deleteById(id);
    }
}