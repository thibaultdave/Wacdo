package com.gdu.wacdo.controllers;

import com.gdu.wacdo.dto.AssignmentRequestDTO;
import com.gdu.wacdo.dto.AssignmentResponseDTO;
import com.gdu.wacdo.services.AssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<AssignmentResponseDTO> findAll() {
        return assignmentService.findAll();
    }

    @GetMapping("/{id}")
    public AssignmentResponseDTO findById(@PathVariable Long id) {
        return assignmentService.findById(id);
    }

    @PostMapping
    public AssignmentResponseDTO create(@RequestBody AssignmentRequestDTO dto) {
        return assignmentService.create(dto);
    }
    @PutMapping("/{id}")
    public AssignmentResponseDTO update(@PathVariable Long id, @RequestBody AssignmentRequestDTO dto) {
        return assignmentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        assignmentService.deleteById(id);
    }
}