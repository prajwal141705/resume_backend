package com.example.resumebuilder.service;

import com.example.resumebuilder.model.Education;
import com.example.resumebuilder.model.Experience;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Project;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMatchingService {

    private final ResumeService resumeService;
    private final LlmService llmService;

    public AiMatchResponse matchResumeWithJob(AiMatchRequest request, String currentUserId) {
        log.info("Processing AI matching for resume {} by user {}", request.getResumeId(), currentUserId);

        // 1. Fetch resume and validate ownership
        Resume resume = resumeService.getResumeEntityById(request.getResumeId(), currentUserId);

        // 2. Convert resume to structured textual format
        String formattedResume = formatResumeForLlm(resume);

        // 3. Delegate to LLM Service for deep semantic matching
        AiMatchResponse response = llmService.analyzeResumeAgainstJob(formattedResume, request.getJobDescription());

        log.info("AI matching complete for resume {}. Match score: {}", request.getResumeId(), response.getMatchScore());
        return response;
    }

    public String formatResumeForLlm(Resume resume) {
        StringBuilder sb = new StringBuilder();

        sb.append("TITLE: ").append(resume.getTitle() != null ? resume.getTitle() : "Untitled Resume").append("\n\n");

        // Personal Info
        if (resume.getPersonalInfo() != null) {
            PersonalInfo pi = resume.getPersonalInfo();
            sb.append("CANDIDATE INFO:\n");
            if (pi.getFullName() != null) sb.append("- Name: ").append(pi.getFullName()).append("\n");
            if (pi.getEmail() != null) sb.append("- Email: ").append(pi.getEmail()).append("\n");
            if (pi.getLocation() != null) sb.append("- Location: ").append(pi.getLocation()).append("\n");
            if (pi.getLinkedin() != null) sb.append("- LinkedIn: ").append(pi.getLinkedin()).append("\n");
            if (pi.getGithub() != null) sb.append("- GitHub: ").append(pi.getGithub()).append("\n");
            sb.append("\n");
        }

        // Summary
        if (resume.getSummary() != null && !resume.getSummary().isBlank()) {
            sb.append("PROFESSIONAL SUMMARY:\n")
                    .append(resume.getSummary())
                    .append("\n\n");
        }

        // Skills
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            sb.append("TECHNICAL & PROFESSIONAL SKILLS:\n")
                    .append(String.join(", ", resume.getSkills()))
                    .append("\n\n");
        }

        // Experience
        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
            sb.append("PROFESSIONAL EXPERIENCE:\n");
            for (Experience exp : resume.getExperience()) {
                sb.append("• Role: ").append(exp.getRole()).append(" at ").append(exp.getCompany()).append("\n");
                sb.append("  Duration: ").append(exp.getStartDate() != null ? exp.getStartDate() : "").append(" - ")
                        .append(exp.isCurrent() ? "Present" : (exp.getEndDate() != null ? exp.getEndDate() : "")).append("\n");
                if (exp.getDescription() != null && !exp.getDescription().isBlank()) {
                    sb.append("  Responsibilities & Achievements:\n  ").append(exp.getDescription().replace("\n", "\n  ")).append("\n");
                }
                sb.append("\n");
            }
        }

        // Projects
        if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
            sb.append("KEY PROJECTS:\n");
            for (Project proj : resume.getProjects()) {
                sb.append("• ").append(proj.getTitle()).append("\n");
                if (proj.getTechStack() != null && !proj.getTechStack().isEmpty()) {
                    sb.append("  Technologies: ").append(String.join(", ", proj.getTechStack())).append("\n");
                }
                if (proj.getDescription() != null && !proj.getDescription().isBlank()) {
                    sb.append("  Details: ").append(proj.getDescription()).append("\n");
                }
                if (proj.getLink() != null && !proj.getLink().isBlank()) {
                    sb.append("  Link: ").append(proj.getLink()).append("\n");
                }
                sb.append("\n");
            }
        }

        // Education
        if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
            sb.append("EDUCATION:\n");
            for (Education edu : resume.getEducation()) {
                sb.append("• ").append(edu.getDegree()).append(" from ").append(edu.getInstitution());
                if (edu.getFieldOfStudy() != null && !edu.getFieldOfStudy().isBlank()) {
                    sb.append(" (").append(edu.getFieldOfStudy()).append(")");
                }
                sb.append("\n");
                sb.append("  Years: ").append(edu.getStartYear() != null ? edu.getStartYear() : "").append(" - ")
                        .append(edu.getEndYear() != null ? edu.getEndYear() : "").append("\n");
                if (edu.getDescription() != null && !edu.getDescription().isBlank()) {
                    sb.append("  Details: ").append(edu.getDescription()).append("\n");
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
