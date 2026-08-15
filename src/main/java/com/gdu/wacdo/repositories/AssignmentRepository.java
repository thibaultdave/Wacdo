package com.gdu.wacdo.repositories;

import com.gdu.wacdo.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}