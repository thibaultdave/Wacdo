package com.gdu.wacdo.services;

import com.gdu.wacdo.constants.ExceptionMessages;
import com.gdu.wacdo.dto.JobRequestDTO;
import com.gdu.wacdo.dto.JobResponseDTO;
import com.gdu.wacdo.entities.Job;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.repositories.JobRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;

    public JobService(JobRepository jobRepository, ModelMapper modelMapper) {
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
    }

    public List<JobResponseDTO> findAll() {
        return jobRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessages.JOB_NOT_FOUND, id
                ));
    }

    public JobResponseDTO findById(Long id) {
        return toResponseDTO(findJobById(id));
    }

    public JobResponseDTO create(JobRequestDTO dto) {

        Job createdJob = jobRepository.save(toEntity(dto));

        return toResponseDTO(createdJob);
    }

    public JobResponseDTO update(Long id, JobRequestDTO dto) {

        Job job = findJobById(id);
        Job updatedJob = jobRepository.save(
                setJobFromRequest(job, dto)
        );

        return toResponseDTO(updatedJob);
    }

    public void deleteById(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    ExceptionMessages.JOB_NOT_FOUND, id
            );
        }

        jobRepository.deleteById(id);
    }

    // DTO METHODS

    private JobResponseDTO toResponseDTO(Job job) {
        return modelMapper.map(job, JobResponseDTO.class);
    }

    private Job toEntity(JobRequestDTO dto) {
        Job job = new Job();

        return setJobFromRequest(job, dto);
    }

    private Job setJobFromRequest(Job job, JobRequestDTO dto) {
        modelMapper.map(dto, job);

        return job;
    }
}