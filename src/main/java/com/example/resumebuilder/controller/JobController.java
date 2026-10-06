package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.JobResponse;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Creating job posting by user: {}", userId);
        JobResponse response = jobService.createJob(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        log.info("Fetching all job postings");
        List<JobResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable String jobId) {
        log.info("Fetching job details for id: {}", jobId);
        JobResponse job = jobService.getJobById(jobId);
        return ResponseEntity.ok(job);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable String jobId,
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Updating job: {} by user: {}", jobId, userId);
        JobResponse updatedJob = jobService.updateJob(jobId, request, userId);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(
            @PathVariable String jobId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Deleting job: {} by user: {}", jobId, userId);
        jobService.deleteJob(jobId, userId);
        return ResponseEntity.noContent().build();
    }
}
