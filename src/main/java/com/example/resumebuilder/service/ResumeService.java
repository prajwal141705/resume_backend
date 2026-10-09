package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.ResumeVersion;
import com.example.resumebuilder.model.dto.ResumeRequest;
import com.example.resumebuilder.model.dto.ResumeResponse;
import com.example.resumebuilder.repository.ResumeRepository;
import com.example.resumebuilder.repository.ResumeVersionRepository;
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
    private final ResumeVersionRepository versionRepository;
    private final NotificationService notificationService;

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
                .accentColor(request.getAccentColor() != null ? request.getAccentColor() : "#4f46e5")
                .fontFamily(request.getFontFamily() != null ? request.getFontFamily() : "Inter, sans-serif")
                .fontSize(request.getFontSize() != null ? request.getFontSize() : "medium")
                .spacing(request.getSpacing() != null ? request.getSpacing() : "normal")
                .margins(request.getMargins() != null ? request.getMargins() : "normal")
                .layout(request.getLayout() != null ? request.getLayout() : "one-column")
                .sectionOrder(request.getSectionOrder() != null ? request.getSectionOrder() : new ArrayList<>())
                .atsScore(request.getAtsScore())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Resume savedResume = resumeRepository.save(resume);

        // Auto-create Version 1
        createVersionSnapshot(savedResume, "Initial Version", "Created initial resume draft", currentUserId);

        // Send In-App Notification
        notificationService.sendNotification(
                currentUserId,
                "Resume Created Successfully",
                "Your resume '" + savedResume.getTitle() + "' was successfully created and ready for ATS analysis.",
                "RESUME_CREATED",
                "/resume-builder?id=" + savedResume.getId()
        );

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
        
        if (request.getAccentColor() != null) resume.setAccentColor(request.getAccentColor());
        if (request.getFontFamily() != null) resume.setFontFamily(request.getFontFamily());
        if (request.getFontSize() != null) resume.setFontSize(request.getFontSize());
        if (request.getSpacing() != null) resume.setSpacing(request.getSpacing());
        if (request.getMargins() != null) resume.setMargins(request.getMargins());
        if (request.getLayout() != null) resume.setLayout(request.getLayout());
        if (request.getSectionOrder() != null) resume.setSectionOrder(request.getSectionOrder());
        if (request.getAtsScore() > 0) resume.setAtsScore(request.getAtsScore());

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
                .accentColor(original.getAccentColor())
                .fontFamily(original.getFontFamily())
                .fontSize(original.getFontSize())
                .spacing(original.getSpacing())
                .margins(original.getMargins())
                .layout(original.getLayout())
                .sectionOrder(original.getSectionOrder() != null ? new ArrayList<>(original.getSectionOrder()) : new ArrayList<>())
                .atsScore(original.getAtsScore())
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Resume saved = resumeRepository.save(duplicate);
        createVersionSnapshot(saved, "Initial Version", "Duplicated from " + original.getTitle(), currentUserId);
        log.info("Duplicated resume {} into new resume {}", resumeId, saved.getId());
        return mapToResponse(saved);
    }

    public void deleteResume(String resumeId, String currentUserId) {
        Resume resume = getResumeAndVerifyOwnership(resumeId, currentUserId);
        versionRepository.deleteByResumeId(resumeId);
        resumeRepository.delete(resume);
        log.info("Resume {} and its versions deleted by user {}", resumeId, currentUserId);
    }

    // --- Version History Management ---
    public ResumeVersion createVersionSnapshot(Resume resume, String versionName, String notes, String currentUserId) {
        long count = versionRepository.countByResumeId(resume.getId());
        int nextVersion = (int) count + 1;

        ResumeVersion version = ResumeVersion.builder()
                .resumeId(resume.getId())
                .userId(currentUserId)
                .versionNumber(nextVersion)
                .versionName(versionName != null && !versionName.isBlank() ? versionName : "Version " + nextVersion)
                .resumeSnapshot(resume)
                .changeNotes(notes)
                .createdAt(Instant.now())
                .build();

        return versionRepository.save(version);
    }

    public ResumeVersion createManualVersion(String resumeId, String versionName, String notes, String currentUserId) {
        Resume resume = getResumeAndVerifyOwnership(resumeId, currentUserId);
        return createVersionSnapshot(resume, versionName, notes, currentUserId);
    }

    public List<ResumeVersion> getResumeVersions(String resumeId, String currentUserId) {
        getResumeAndVerifyOwnership(resumeId, currentUserId);
        return versionRepository.findByResumeIdOrderByVersionNumberDesc(resumeId);
    }

    public ResumeResponse restoreResumeVersion(String resumeId, String versionId, String currentUserId) {
        Resume currentResume = getResumeAndVerifyOwnership(resumeId, currentUserId);
        ResumeVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume version not found: " + versionId));

        if (!version.getResumeId().equals(resumeId)) {
            throw new ForbiddenException("Version does not belong to this resume");
        }

        Resume snapshot = version.getResumeSnapshot();
        if (snapshot != null) {
            currentResume.setTitle(snapshot.getTitle());
            currentResume.setTemplate(snapshot.getTemplate());
            currentResume.setPersonalInfo(snapshot.getPersonalInfo());
            currentResume.setSummary(snapshot.getSummary());
            currentResume.setCareerObjective(snapshot.getCareerObjective());
            currentResume.setEducation(snapshot.getEducation());
            currentResume.setExperience(snapshot.getExperience());
            currentResume.setInternships(snapshot.getInternships());
            currentResume.setProjects(snapshot.getProjects());
            currentResume.setSkills(snapshot.getSkills());
            currentResume.setTechnicalSkills(snapshot.getTechnicalSkills());
            currentResume.setCertifications(snapshot.getCertifications());
            currentResume.setAchievements(snapshot.getAchievements());
            currentResume.setLanguages(snapshot.getLanguages());
            currentResume.setHobbies(snapshot.getHobbies());
            currentResume.setCustomSections(snapshot.getCustomSections());
            currentResume.setAccentColor(snapshot.getAccentColor());
            currentResume.setFontFamily(snapshot.getFontFamily());
            currentResume.setFontSize(snapshot.getFontSize());
            currentResume.setSpacing(snapshot.getSpacing());
            currentResume.setMargins(snapshot.getMargins());
            currentResume.setLayout(snapshot.getLayout());
            currentResume.setSectionOrder(snapshot.getSectionOrder());
            currentResume.setAtsScore(snapshot.getAtsScore());
            currentResume.setUpdatedAt(Instant.now());

            Resume saved = resumeRepository.save(currentResume);
            createVersionSnapshot(saved, "Restored Version " + version.getVersionNumber(), "Restored from " + version.getVersionName(), currentUserId);
            return mapToResponse(saved);
        }

        return mapToResponse(currentResume);
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
                .accentColor(resume.getAccentColor())
                .fontFamily(resume.getFontFamily())
                .fontSize(resume.getFontSize())
                .spacing(resume.getSpacing())
                .margins(resume.getMargins())
                .layout(resume.getLayout())
                .sectionOrder(resume.getSectionOrder())
                .atsScore(resume.getAtsScore())
                .status(resume.getStatus())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();
    }
}
