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
                .template(request.getTemplate() != null ? request.getTemplate() : "modern")
                .personalInfo(request.getPersonalInfo())
                .summary(request.getSummary())
                .careerObjective(request.getCareerObjective())
                .education(request.getEducation() != null ? request.getEducation() : new ArrayList<>())
                .experience(request.getExperience() != null ? request.getExperience() : new ArrayList<>())
                .internships(request.getInternships() != null ? request.getInternships() : new ArrayList<>())
                .projects(request.getProjects() != null ? request.getProjects() : new ArrayList<>())
                .skills(request.getSkills() != null ? request.getSkills() : new ArrayList<>())
                .technicalSkills(request.getTechnicalSkills() != null ? request.getTechnicalSkills() : new ArrayList<>())
                .certifications(request.getCertifications() != null ? request.getCertifications() : new ArrayList<>())
                .achievements(request.getAchievements() != null ? request.getAchievements() : new ArrayList<>())
                .languages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>())
                .hobbies(request.getHobbies() != null ? request.getHobbies() : new ArrayList<>())
                .customSections(request.getCustomSections() != null ? request.getCustomSections() : new ArrayList<>())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
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

        return resumeRepository.findByUserIdOrderByUpdatedAtDesc(targetUserId).stream()
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
        if (request.getTemplate() != null) resume.setTemplate(request.getTemplate());
        resume.setPersonalInfo(request.getPersonalInfo());
        resume.setSummary(request.getSummary());
        resume.setCareerObjective(request.getCareerObjective());
        resume.setEducation(request.getEducation() != null ? request.getEducation() : new ArrayList<>());
        resume.setExperience(request.getExperience() != null ? request.getExperience() : new ArrayList<>());
        resume.setInternships(request.getInternships() != null ? request.getInternships() : new ArrayList<>());
        resume.setProjects(request.getProjects() != null ? request.getProjects() : new ArrayList<>());
        resume.setSkills(request.getSkills() != null ? request.getSkills() : new ArrayList<>());
        resume.setTechnicalSkills(request.getTechnicalSkills() != null ? request.getTechnicalSkills() : new ArrayList<>());
        resume.setCertifications(request.getCertifications() != null ? request.getCertifications() : new ArrayList<>());
        resume.setAchievements(request.getAchievements() != null ? request.getAchievements() : new ArrayList<>());
        resume.setLanguages(request.getLanguages() != null ? request.getLanguages() : new ArrayList<>());
        resume.setHobbies(request.getHobbies() != null ? request.getHobbies() : new ArrayList<>());
        resume.setCustomSections(request.getCustomSections() != null ? request.getCustomSections() : new ArrayList<>());
        if (request.getStatus() != null) resume.setStatus(request.getStatus());
        resume.setUpdatedAt(Instant.now());

        Resume updatedResume = resumeRepository.save(resume);
        log.info("Resume {} updated by user {}", resumeId, currentUserId);
        return mapToResponse(updatedResume);
    }

    public ResumeResponse duplicateResume(String resumeId, String currentUserId) {
        Resume original = getResumeAndVerifyOwnership(resumeId, currentUserId);

        Resume duplicate = Resume.builder()
                .userId(currentUserId)
                .title(original.getTitle() + " (Copy)")
                .template(original.getTemplate())
                .personalInfo(original.getPersonalInfo())
                .summary(original.getSummary())
                .careerObjective(original.getCareerObjective())
                .education(original.getEducation() != null ? new ArrayList<>(original.getEducation()) : new ArrayList<>())
                .experience(original.getExperience() != null ? new ArrayList<>(original.getExperience()) : new ArrayList<>())
                .internships(original.getInternships() != null ? new ArrayList<>(original.getInternships()) : new ArrayList<>())
                .projects(original.getProjects() != null ? new ArrayList<>(original.getProjects()) : new ArrayList<>())
                .skills(original.getSkills() != null ? new ArrayList<>(original.getSkills()) : new ArrayList<>())
                .technicalSkills(original.getTechnicalSkills() != null ? new ArrayList<>(original.getTechnicalSkills()) : new ArrayList<>())
                .certifications(original.getCertifications() != null ? new ArrayList<>(original.getCertifications()) : new ArrayList<>())
                .achievements(original.getAchievements() != null ? new ArrayList<>(original.getAchievements()) : new ArrayList<>())
                .languages(original.getLanguages() != null ? new ArrayList<>(original.getLanguages()) : new ArrayList<>())
                .hobbies(original.getHobbies() != null ? new ArrayList<>(original.getHobbies()) : new ArrayList<>())
                .customSections(original.getCustomSections() != null ? new ArrayList<>(original.getCustomSections()) : new ArrayList<>())
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Resume saved = resumeRepository.save(duplicate);
        log.info("Duplicated resume {} into new resume {}", resumeId, saved.getId());
        return mapToResponse(saved);
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
                .template(resume.getTemplate() != null ? resume.getTemplate() : "modern")
                .personalInfo(resume.getPersonalInfo())
                .summary(resume.getSummary())
                .careerObjective(resume.getCareerObjective())
                .education(resume.getEducation())
                .experience(resume.getExperience())
                .internships(resume.getInternships())
                .projects(resume.getProjects())
                .skills(resume.getSkills())
                .technicalSkills(resume.getTechnicalSkills())
                .certifications(resume.getCertifications())
                .achievements(resume.getAchievements())
                .languages(resume.getLanguages())
                .hobbies(resume.getHobbies())
                .customSections(resume.getCustomSections())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }
}
