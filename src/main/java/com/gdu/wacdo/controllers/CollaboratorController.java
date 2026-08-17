package com.gdu.wacdo.controllers;

import com.gdu.wacdo.entities.Collaborator;
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
    public List<Collaborator> findAll() {
        return collaboratorService.findAll();
    }

    @GetMapping("/{id}")
    public Collaborator findById(@PathVariable Long id) {
        return collaboratorService.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Collaborateur introuvable avec l'id : " + id
                ));
    }

    @PostMapping
    public Collaborator save(@RequestBody Collaborator collaborator) {
        return collaboratorService.save(collaborator);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        collaboratorService.deleteById(id);
    }
}