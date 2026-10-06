package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.ResumeRequest;
import com.example.resumebuilder.model.dto.ResumeResponse;
import com.example.resumebuilder.repository.ResumeRepository;
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
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeResponse createResume(ResumeRequest request, String currentUserId) {
        log.info("Creating resume for user: {}", currentUserId);

        Resume resume = Resume.builder()
                .userId(currentUserId)
                .title(request.getTitle())
                .personalInfo(request.getPersonalInfo())
                .summary(request.getSummary())
                .education(request.getEducation() != null ? request.getEducation() : new ArrayList<>())
                .experience(request.getExperience() != null ? request.getExperience() : new ArrayList<>())
                .projects(request.getProjects() != null ? request.getProjects() : new ArrayList<>())
                .skills(request.getSkills() != null ? request.getSkills() : new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Resume savedResume = resumeRepository.save(resume);
        log.info("Resume created with id: {}", savedResume.getId());
        return mapToResponse(savedResume);
    }

    public List<ResumeResponse> getResumesByUserId(String targetUserId, String currentUserId) {
        if (!targetUserId.equals(currentUserId)) {
            log.warn("Unauthorized access attempt: user {} tried to access resumes of user {}", currentUserId, targetUserId);
            throw new ForbiddenException("Access denied: You can only view your own resumes");
        }

        return resumeRepository.findByUserId(targetUserId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResumeResponse getResumeById(String resumeId, String currentUserId) {
        Resume resume = getResumeAndVerifyOwnership(resumeId, currentUserId);
        return mapToResponse(resume);
    }

    public Resume getResumeEntityById(String resumeId, String currentUserId) {
        return getResumeAndVerifyOwnership(resumeId, currentUserId);
    }

    public ResumeResponse updateResume(String resumeId, ResumeRequest request, String currentUserId) {
        Resume resume = getResumeAndVerifyOwnership(resumeId, currentUserId);

        resume.setTitle(request.getTitle());
        resume.setPersonalInfo(request.getPersonalInfo());
        resume.setSummary(request.getSummary());
        resume.setEducation(request.getEducation() != null ? request.getEducation() : new ArrayList<>());
        resume.setExperience(request.getExperience() != null ? request.getExperience() : new ArrayList<>());
        resume.setProjects(request.getProjects() != null ? request.getProjects() : new ArrayList<>());
        resume.setSkills(request.getSkills() != null ? request.getSkills() : new ArrayList<>());
        resume.setUpdatedAt(Instant.now());

        Resume updatedResume = resumeRepository.save(resume);
        log.info("Resume {} updated by user {}", resumeId, currentUserId);
        return mapToResponse(updatedResume);
    }

    public void deleteResume(String resumeId, String currentUserId) {
        Resume resume = getResumeAndVerifyOwnership(resumeId, currentUserId);
        resumeRepository.delete(resume);
        log.info("Resume {} deleted by user {}", resumeId, currentUserId);
    }

    private Resume getResumeAndVerifyOwnership(String resumeId, String currentUserId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));

        if (!resume.getUserId().equals(currentUserId)) {
            log.warn("Unauthorized access: user {} attempted to access resume {} owned by user {}",
                    currentUserId, resumeId, resume.getUserId());
            throw new ForbiddenException("Access denied: You do not have permission to access this resume");
        }

        return resume;
    }

    public ResumeResponse mapToResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .userId(resume.getUserId())
                .title(resume.getTitle())
                .personalInfo(resume.getPersonalInfo())
                .summary(resume.getSummary())
                .education(resume.getEducation())
                .experience(resume.getExperience())
                .projects(resume.getProjects())
                .skills(resume.getSkills())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }
}
