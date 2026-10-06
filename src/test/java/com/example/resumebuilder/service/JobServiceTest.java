package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.Job;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.JobResponse;
import com.example.resumebuilder.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private Job sampleJob;

    @BeforeEach
    void setUp() {
        sampleJob = Job.builder()
                .id("job123")
                .title("Senior Java Developer")
                .company("Acme Corp")
                .description("We are looking for a senior developer with Spring Boot experience.")
                .requiredSkills(List.of("Java", "Spring Boot", "MongoDB"))
                .location("Remote")
                .jobType("Full-Time")
                .createdBy("recruiter123")
                .postedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createJob_Success() {
        JobRequest request = JobRequest.builder()
                .title("Senior Java Developer")
                .company("Acme Corp")
                .description("We are looking for a senior developer with Spring Boot experience.")
                .requiredSkills(List.of("Java", "Spring Boot", "MongoDB"))
                .location("Remote")
                .build();

        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobResponse response = jobService.createJob(request, "recruiter123");

        assertNotNull(response);
        assertEquals("job123", response.getId());
        assertEquals("Acme Corp", response.getCompany());
    }

    @Test
    void getAllJobs_Success() {
        when(jobRepository.findByEnabledTrueOrderByPostedDateDesc()).thenReturn(List.of(sampleJob));

        List<JobResponse> jobs = jobService.getAllJobs();

        assertEquals(1, jobs.size());
        assertEquals("job123", jobs.get(0).getId());
    }

    @Test
    void getJobById_Success() {
        when(jobRepository.findById("job123")).thenReturn(Optional.of(sampleJob));

        JobResponse response = jobService.getJobById("job123");

        assertNotNull(response);
        assertEquals("job123", response.getId());
    }

    @Test
    void getJobById_NotFound_ThrowsResourceNotFoundException() {
        when(jobRepository.findById("unknownJob")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.getJobById("unknownJob"));
    }

    @Test
    void updateJob_Owner_Success() {
        JobRequest updateRequest = JobRequest.builder()
                .title("Lead Java Developer")
                .company("Acme Corp")
                .description("Updated job description")
                .build();

        when(jobRepository.findById("job123")).thenReturn(Optional.of(sampleJob));
        when(jobRepository.save(any(Job.class))).thenReturn(sampleJob);

        JobResponse response = jobService.updateJob("job123", updateRequest, "recruiter123");

        assertNotNull(response);
        verify(jobRepository).save(sampleJob);
    }

    @Test
    void updateJob_NonOwner_ThrowsForbiddenException() {
        JobRequest updateRequest = JobRequest.builder()
                .title("Lead Java Developer")
                .company("Acme Corp")
                .description("Updated description")
                .build();

        when(jobRepository.findById("job123")).thenReturn(Optional.of(sampleJob));

        assertThrows(ForbiddenException.class, () -> jobService.updateJob("job123", updateRequest, "otherUser999"));
    }
}
