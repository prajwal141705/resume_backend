package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.Job;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.JobResponse;
import com.example.resumebuilder.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public JobResponse createJob(JobRequest request, String currentUserId) {
        log.info("Creating job posting by user: {}", currentUserId);

        Job job = Job.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .description(request.getDescription())
                .requiredSkills(request.getRequiredSkills() != null ? request.getRequiredSkills() : new ArrayList<>())
                .location(request.getLocation() != null ? request.getLocation() : "Remote")
                .jobType(request.getJobType() != null ? request.getJobType() : "Full-time")
                .salaryRange(request.getSalaryRange())
                .experienceLevel("Mid-Level")
                .enabled(true)
                .createdBy(currentUserId)
                .postedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Job created with id: {}", savedJob.getId());
        return mapToResponse(savedJob);
    }

    public List<JobResponse> getAllJobs() {
        return jobRepository.findByEnabledTrueOrderByPostedDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> searchJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllJobs();
        }
        return jobRepository.findByEnabledTrueAndTitleContainingIgnoreCaseOrEnabledTrueAndCompanyContainingIgnoreCase(keyword.trim(), keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobResponse getJobById(String jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return mapToResponse(job);
    }

    public JobResponse updateJob(String jobId, JobRequest request, String currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        // Allow creator or users with administrative roles
        if (job.getCreatedBy() != null && !job.getCreatedBy().equals(currentUserId)) {
            log.warn("Unauthorized attempt to update job {} by user {}", jobId, currentUserId);
            throw new ForbiddenException("Access denied: You do not have permission to update this job");
        }

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills() != null ? request.getRequiredSkills() : new ArrayList<>());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalaryRange(request.getSalaryRange());
        job.setUpdatedAt(Instant.now());

        Job updatedJob = jobRepository.save(job);
        log.info("Job {} updated successfully", jobId);
        return mapToResponse(updatedJob);
    }

    public void deleteJob(String jobId, String currentUserId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

        if (job.getCreatedBy() != null && !job.getCreatedBy().equals(currentUserId)) {
            log.warn("Unauthorized attempt to delete job {} by user {}", jobId, currentUserId);
            throw new ForbiddenException("Access denied: You do not have permission to delete this job");
        }

        jobRepository.delete(job);
        log.info("Job {} deleted successfully", jobId);
    }

    public JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .description(job.getDescription())
                .requiredSkills(job.getRequiredSkills())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .salaryRange(job.getSalaryRange())
                .createdBy(job.getCreatedBy())
                .postedDate(job.getPostedDate())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
