package com.gdu.wacdo.controllers;

import com.gdu.wacdo.dto.JobRequestDTO;
import com.gdu.wacdo.dto.JobResponseDTO;
import com.gdu.wacdo.services.JobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public List<JobResponseDTO> findAll() {
        return jobService.findAll();
    }

    @GetMapping("/{id}")
    public JobResponseDTO findById(@PathVariable Long id) {
        return jobService.findById(id);
    }

    @PostMapping
    public JobResponseDTO create(@RequestBody JobRequestDTO dto) {
        return jobService.create(dto);
    }

    @PutMapping("/{id}")
    public JobResponseDTO update(@PathVariable Long id, @RequestBody JobRequestDTO dto) {
        return jobService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        jobService.deleteById(id);
    }
}