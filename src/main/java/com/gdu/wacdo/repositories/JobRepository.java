package com.gdu.wacdo.repositories;

import com.gdu.wacdo.entities.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}