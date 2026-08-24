package com.gdu.wacdo.repositories;

import com.gdu.wacdo.entities.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollaboratorRepository extends JpaRepository<Collaborator, Long> {
    Optional<Collaborator> findByEmail(String email);
}