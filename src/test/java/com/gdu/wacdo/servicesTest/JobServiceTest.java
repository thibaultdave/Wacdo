package com.gdu.wacdo.servicesTest;

import com.gdu.wacdo.entities.Job;
import com.gdu.wacdo.exceptions.ResourceNotFoundException;
import com.gdu.wacdo.repositories.JobRepository;
import com.gdu.wacdo.services.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ModelMapper modelMapper;


    private JobService jobService;

    @BeforeEach
    void setUp() {
        jobService = new JobService(
                jobRepository,
                modelMapper
        );
    }

    // FIND JOB BY ID

    @Test
    void findJobById_shouldReturnJob_whenIdExists() {
        // Arrange
        Long id = 1L;

        Job job = new Job();
        job.setId(id);

        when(jobRepository.findById(id))
                .thenReturn(Optional.of(job));

        // Act
        Job result = jobService.findJobById(id);

        // Assert
        assertThat(result).isSameAs(job);
    }

    @Test
    void findJobById_shouldThrowException_whenIdDoesNotExist() {
        // Arrange
        Long id = 999L;

        when(jobRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(
                () -> jobService.findJobById(id)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

}