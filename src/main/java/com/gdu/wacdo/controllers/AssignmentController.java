package com.gdu.wacdo.controllers;

import com.gdu.wacdo.entities.Assignment;
import com.gdu.wacdo.services.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public List<Assignment> findAll() {
        return assignmentService.findAll();
    }

    @GetMapping("/{id}")
    public Assignment findById(@PathVariable Long id) {
        return assignmentService.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Collaborateur introuvable avec l'id : " + id
                ));
    }

    @PostMapping
    public Assignment save(@RequestBody Assignment assignment) {
        return assignmentService.save(assignment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        assignmentService.deleteById(id);
    }
}