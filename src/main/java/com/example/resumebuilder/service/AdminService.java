package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.BadRequestException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.Job;
import com.example.resumebuilder.model.ResumeTemplate;
import com.example.resumebuilder.model.UploadedResume;
import com.example.resumebuilder.model.User;
import com.example.resumebuilder.model.UserResumeTemplateRequest;
import com.example.resumebuilder.model.dto.AdminStatsResponse;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.UserDto;
import com.example.resumebuilder.repository.JobRepository;
import com.example.resumebuilder.repository.ResumeRepository;
import com.example.resumebuilder.repository.ResumeTemplateRepository;
import com.example.resumebuilder.repository.UploadedResumeRepository;
import com.example.resumebuilder.repository.UserRepository;
import com.example.resumebuilder.repository.UserResumeTemplateRequestRepository;
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
public class AdminService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final ResumeTemplateRepository templateRepository;
    private final UploadedResumeRepository uploadedResumeRepository;
    private final UserResumeTemplateRequestRepository templateRequestRepository;

    public AdminStatsResponse getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus("ACTIVE");
        long totalResumes = resumeRepository.count();
        long totalUploads = uploadedResumeRepository.count();
        long totalTemplates = templateRepository.count();
        long totalJobs = jobRepository.count();
        long pendingRequests = templateRequestRepository.countByStatus("PENDING");

        List<UserDto> recentUsers = userRepository.findAll().stream()
                .sorted((a, b) -> (b.getCreatedAt() != null ? b.getCreatedAt() : Instant.EPOCH)
                        .compareTo(a.getCreatedAt() != null ? a.getCreatedAt() : Instant.EPOCH))
                .limit(5)
                .map(this::mapUserToDto)
                .collect(Collectors.toList());

        List<UploadedResume> recentUploads = uploadedResumeRepository.findAllByOrderByCreatedAtDesc().stream()
                .limit(5)
                .collect(Collectors.toList());

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalResumes(totalResumes)
                .totalUploadedResumes(totalUploads)
                .totalTemplates(totalTemplates)
                .totalJobs(totalJobs)
                .pendingTemplateRequests(pendingRequests)
                .recentUsers(recentUsers)
                .recentUploads(recentUploads)
                .build();
    }

    public List<UserDto> getAllUsers(String search, String status) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search.trim(), search.trim());
        } else if (status != null && !status.trim().isEmpty()) {
            users = userRepository.findByStatus(status.trim().toUpperCase());
        } else {
            users = userRepository.findAll();
        }

        return users.stream().map(this::mapUserToDto).collect(Collectors.toList());
    }

    public UserDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapUserToDto(user);
    }

    public UserDto updateUserStatus(String id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setStatus(status.toUpperCase());
        user.setUpdatedAt(Instant.now());
        User saved = userRepository.save(user);
        log.info("Admin updated user {} status to {}", id, status);
        return mapUserToDto(saved);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getRoles().contains("ROLE_ADMIN")) {
            throw new BadRequestException("Admin accounts cannot be deleted directly.");
        }

        userRepository.delete(user);
        log.info("Admin deleted user {}", id);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAllByOrderByPostedDateDesc();
    }

    public Job createJob(JobRequest request, String adminId) {
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
                .createdBy(adminId)
                .postedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return jobRepository.save(job);
    }

    public Job updateJob(String id, JobRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setDescription(request.getDescription());
        job.setRequiredSkills(request.getRequiredSkills() != null ? request.getRequiredSkills() : new ArrayList<>());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setSalaryRange(request.getSalaryRange());
        job.setUpdatedAt(Instant.now());

        return jobRepository.save(job);
    }

    public Job toggleJobStatus(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        job.setEnabled(!job.isEnabled());
        job.setUpdatedAt(Instant.now());
        return jobRepository.save(job);
    }

    public void deleteJob(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
        jobRepository.delete(job);
    }

    public List<ResumeTemplate> getAllTemplates() {
        return templateRepository.findAllByOrderByCreatedAtAsc();
    }

    public ResumeTemplate createTemplate(ResumeTemplate template, String adminId) {
        template.setCreatedBy(adminId);
        template.setCreatedAt(Instant.now());
        template.setUpdatedAt(Instant.now());
        return templateRepository.save(template);
    }

    public ResumeTemplate updateTemplate(String id, ResumeTemplate updated) {
        ResumeTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        template.setName(updated.getName());
        template.setDescription(updated.getDescription());
        template.setCategory(updated.getCategory());
        template.setPreviewUrl(updated.getPreviewUrl());
        template.setEnabled(updated.isEnabled());
        template.setUpdatedAt(Instant.now());

        return templateRepository.save(template);
    }

    public ResumeTemplate toggleTemplateStatus(String id) {
        ResumeTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        template.setEnabled(!template.isEnabled());
        template.setUpdatedAt(Instant.now());
        return templateRepository.save(template);
    }

    public void deleteTemplate(String id) {
        ResumeTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));
        templateRepository.delete(template);
    }

    public List<UserResumeTemplateRequest> getAllTemplateRequests(String status) {
        if (status != null && !status.trim().isEmpty()) {
            return templateRequestRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());
        }
        return templateRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public UserResumeTemplateRequest reviewTemplateRequest(String requestId, String status, String feedback, String adminId) {
        UserResumeTemplateRequest request = templateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Template request not found with id: " + requestId));

        request.setStatus(status.toUpperCase());
        request.setAdminFeedback(feedback);
        request.setReviewedBy(adminId);
        request.setReviewedAt(Instant.now());

        UserResumeTemplateRequest saved = templateRequestRepository.save(request);

        // If approved, create the template in ResumeTemplate collection automatically
        if ("APPROVED".equalsIgnoreCase(status)) {
            String slug = request.getTemplateName().toLowerCase().replaceAll("[^a-z0-9]", "-");
            if (!templateRepository.existsBySlug(slug)) {
                ResumeTemplate newTemplate = ResumeTemplate.builder()
                        .name(request.getTemplateName())
                        .slug(slug)
                        .description(request.getDescription())
                        .category("Community")
                        .previewUrl(request.getPreviewUrl())
                        .enabled(true)
                        .isCustom(true)
                        .createdBy(request.getUserId())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                templateRepository.save(newTemplate);
                log.info("Approved template request converted to active template: {}", slug);
            }
        }

        return saved;
    }

    public List<UploadedResume> getAllUploadedResumes() {
        return uploadedResumeRepository.findAllByOrderByCreatedAtDesc();
    }

    private UserDto mapUserToDto(User user) {
        long resumeCount = resumeRepository.countByUserId(user.getId());
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus() != null ? user.getStatus() : "ACTIVE")
                .roles(user.getRoles())
                .resumeCount(resumeCount)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
