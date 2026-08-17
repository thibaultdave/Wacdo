package com.gdu.wacdo.services;

import com.gdu.wacdo.entities.Collaborator;
import com.gdu.wacdo.repositories.CollaboratorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    public CollaboratorService(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    public List<Collaborator> findAll() {
        return collaboratorRepository.findAll();
    }

    public Optional<Collaborator> findById(Long id) {
        return collaboratorRepository.findById(id);
    }

    public Collaborator save(Collaborator collaborator) {
        return collaboratorRepository.save(collaborator);
    }

    public void deleteById(Long id) {
        collaboratorRepository.deleteById(id);
    }
}