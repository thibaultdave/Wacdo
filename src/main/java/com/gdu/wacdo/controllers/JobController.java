package com.gdu.wacdo.controllers;

import com.gdu.wacdo.entities.Job;
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
    public List<Job> findAll() {
        return jobService.findAll();
    }

    @GetMapping("/{id}")
    public Job findById(@PathVariable Long id) {
        return jobService.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Collaborateur introuvable avec l'id : " + id
                ));
    }

    @PostMapping
    public Job save(@RequestBody Job job) {
        return jobService.save(job);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        jobService.deleteById(id);
    }
}