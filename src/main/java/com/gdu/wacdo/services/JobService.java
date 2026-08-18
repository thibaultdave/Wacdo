package com.gdu.wacdo.services;

import com.gdu.wacdo.dto.JobRequestDTO;
import com.gdu.wacdo.dto.JobResponseDTO;
import com.gdu.wacdo.entities.Job;
import com.gdu.wacdo.repositories.JobRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<JobResponseDTO> findAll() {
        return jobRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Fonction introuvable avec l'id : " + id
                ));
    }

    public JobResponseDTO findById(Long id) {
        return toResponseDTO(findJobById(id));
    }

    public JobResponseDTO create(JobRequestDTO dto) {

        Job job = toEntity(dto);
        Job createdJob = jobRepository.save(job);

        return toResponseDTO(createdJob);
    }

    public JobResponseDTO update(Long id, JobRequestDTO dto) {

        Job collaborator = findJobById(id);

        Job updatedJob = jobRepository.save(
                setJobFromRequest(collaborator, dto)
        );

        return toResponseDTO(updatedJob);
    }

    public void deleteById(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException(
                    "Fonction introuvable avec l'id : " + id
            );
        }

        jobRepository.deleteById(id);
    }

    // DTO METHODS

    private JobResponseDTO toResponseDTO(Job job) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(job, JobResponseDTO.class);
    }

    private Job toEntity(JobRequestDTO dto) {
        Job job = new Job();

        return setJobFromRequest(job, dto);
    }

    private Job setJobFromRequest(Job job, JobRequestDTO dto) {
        ModelMapper modelMapper = new ModelMapper();

        return modelMapper.map(dto, Job.class);
    }
}