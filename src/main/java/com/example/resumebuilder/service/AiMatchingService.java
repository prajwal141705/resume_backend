package com.example.resumebuilder.service;

import com.example.resumebuilder.model.Education;
import com.example.resumebuilder.model.Experience;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Project;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.AiAssistDtos;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.model.dto.AtsAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMatchingService {

    private final ResumeService resumeService;
    private final LlmService llmService;

    public AiMatchResponse matchResumeWithJob(AiMatchRequest request, String currentUserId) {
        log.info("Processing AI matching for resume {} by user {}", request.getResumeId(), currentUserId);
        Resume resume = resumeService.getResumeEntityById(request.getResumeId(), currentUserId);
        String formattedResume = formatResumeForLlm(resume);
        return llmService.analyzeResumeAgainstJob(formattedResume, request.getJobDescription());
    }

    public AtsAnalysisResponse analyzeAts(String resumeId, String currentUserId) {
        Resume resume = resumeService.getResumeEntityById(resumeId, currentUserId);
        return calculateAtsScore(resume);
    }

    public AtsAnalysisResponse analyzeAtsDirect(Resume resume) {
        return calculateAtsScore(resume);
    }

    public AtsAnalysisResponse calculateAtsScore(Resume resume) {
        Map<String, Integer> categoryScores = new LinkedHashMap<>();
        List<String> strongAreas = new ArrayList<>();
        List<String> areasToImprove = new ArrayList<>();
        List<String> missingInformation = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // 1. Contact Information (Max 15 pts)
        int contactScore = 0;
        PersonalInfo info = resume.getPersonalInfo();
        if (info != null) {
            if (info.getFullName() != null && !info.getFullName().isBlank()) contactScore += 3;
            if (info.getEmail() != null && info.getEmail().contains("@")) contactScore += 4;
            if (info.getPhone() != null && !info.getPhone().isBlank()) contactScore += 3;
            if (info.getLocation() != null && !info.getLocation().isBlank()) contactScore += 2;
            if (info.getLinkedin() != null && !info.getLinkedin().isBlank()) contactScore += 3;
            if (info.getGithub() != null && !info.getGithub().isBlank()) contactScore += 2;
        }
        contactScore = Math.min(15, contactScore);
        categoryScores.put("Contact Information", (int) Math.round((contactScore / 15.0) * 100));
        if (contactScore >= 12) {
            strongAreas.add("Contact details are thorough and professional.");
        } else {
            if (info == null || info.getEmail() == null) missingInformation.add("Professional Email Address");
            if (info == null || info.getLinkedin() == null || info.getLinkedin().isBlank()) missingInformation.add("LinkedIn Profile Link");
            if (info == null || info.getPhone() == null || info.getPhone().isBlank()) missingInformation.add("Phone Number");
            areasToImprove.add("Ensure all primary contact details (LinkedIn, Location, Phone) are filled.");
        }

        // 2. Resume Structure (Max 15 pts)
        int structureScore = 5;
        if (resume.getSummary() != null && !resume.getSummary().isBlank()) structureScore += 3;
        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) structureScore += 3;
        if (resume.getEducation() != null && !resume.getEducation().isEmpty()) structureScore += 2;
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) structureScore += 2;
        categoryScores.put("Resume Structure", (int) Math.round((structureScore / 15.0) * 100));
        if (structureScore >= 13) {
            strongAreas.add("Well-structured hierarchy with distinct, ATS-friendly section blocks.");
        } else {
            areasToImprove.add("Add missing core sections like Summary or Work Experience for optimal parsing.");
        }

        // 3. Skills Coverage (Max 15 pts)
        int skillsScore = 0;
        int totalSkills = (resume.getSkills() != null ? resume.getSkills().size() : 0)
                + (resume.getTechnicalSkills() != null ? resume.getTechnicalSkills().size() : 0);
        if (totalSkills >= 8) skillsScore = 15;
        else if (totalSkills >= 5) skillsScore = 12;
        else if (totalSkills >= 3) skillsScore = 8;
        else if (totalSkills > 0) skillsScore = 4;
        categoryScores.put("Skills & Competencies", (int) Math.round((skillsScore / 15.0) * 100));
        if (skillsScore >= 12) {
            strongAreas.add("Solid inventory of " + totalSkills + " recognized industry skills.");
        } else {
            areasToImprove.add("Add more technical and domain-specific skills (aim for 8+ keywords).");
            suggestions.add("Add relevant framework and tool keywords like React, Spring Boot, Docker, Git, or AWS.");
        }

        // 4. Work Experience & Impact (Max 20 pts)
        int expScore = 0;
        int expCount = resume.getExperience() != null ? resume.getExperience().size() : 0;
        if (expCount >= 2) expScore += 10;
        else if (expCount == 1) expScore += 7;
        else missingInformation.add("Work Experience Records");

        // Check for action verbs and metrics in experience descriptions
        boolean hasMetrics = false;
        boolean hasActionVerbs = false;
        if (resume.getExperience() != null) {
            for (Experience exp : resume.getExperience()) {
                String desc = exp.getDescription() != null ? exp.getDescription().toLowerCase() : "";
                if (Pattern.compile("\\d+%|\\$\\d+|\\d+x|\\d+\\+").matcher(desc).find()) {
                    hasMetrics = true;
                }
                if (Pattern.compile("\\b(led|built|architected|developed|improved|reduced|increased|delivered|launched|designed)\\b").matcher(desc).find()) {
                    hasActionVerbs = true;
                }
            }
        }
        if (hasMetrics) expScore += 5;
        if (hasActionVerbs) expScore += 5;
        categoryScores.put("Experience & Impact", (int) Math.round((expScore / 20.0) * 100));
        if (expScore >= 16) {
            strongAreas.add("Experience descriptions use active power verbs and quantifiable impact metrics.");
        } else {
            if (!hasMetrics) {
                areasToImprove.add("Add measurable metrics (e.g., 'Improved API latency by 35%').");
                suggestions.add("Quantify your achievements with percentages, team size, revenue, or speed metrics.");
            }
            if (!hasActionVerbs) {
                areasToImprove.add("Begin bullet points with strong action verbs (Led, Architected, Designed).");
            }
        }

        // 5. Education & Credentials (Max 10 pts)
        int eduScore = 0;
        int eduCount = resume.getEducation() != null ? resume.getEducation().size() : 0;
        if (eduCount >= 1) eduScore += 7;
        int certCount = resume.getCertifications() != null ? resume.getCertifications().size() : 0;
        if (certCount >= 1) eduScore += 3;
        categoryScores.put("Education & Certifications", (int) Math.round((eduScore / 10.0) * 100));
        if (eduScore >= 8) {
            strongAreas.add("Verified education credentials and certifications.");
        } else {
            if (eduCount == 0) missingInformation.add("Education Section");
            if (certCount == 0) suggestions.add("Add industry certifications (AWS, Scrum, Oracle, Google Cloud, etc.).");
        }

        // 6. Keywords & Terminology (Max 10 pts)
        int keywordScore = Math.min(10, (totalSkills >= 6 ? 6 : totalSkills) + (hasActionVerbs ? 4 : 2));
        categoryScores.put("ATS Keywords", (int) Math.round((keywordScore / 10.0) * 100));

        // 7. Formatting & Parseability (Max 10 pts)
        int formatScore = 10;
        categoryScores.put("Formatting & Clean Layout", 95);

        // 8. Readability & Summary (Max 5 pts)
        int readabilityScore = 3;
        if (resume.getSummary() != null && resume.getSummary().length() > 50) {
            readabilityScore += 2;
            strongAreas.add("Concise professional summary sets clear positioning.");
        } else {
            areasToImprove.add("Summary is short or missing; create a 3-4 sentence professional summary.");
            suggestions.add("Use the AI Summary Generator to produce an executive overview.");
        }
        categoryScores.put("Readability & Summary", (int) Math.round((readabilityScore / 5.0) * 100));

        // Compute overall score (0 - 100)
        int overall = contactScore + structureScore + skillsScore + expScore + eduScore + keywordScore + formatScore + readabilityScore;
        overall = Math.max(20, Math.min(98, overall));

        String grade = overall >= 85 ? "Excellent (ATS Ready)" : overall >= 70 ? "Good (Minor Fixes Needed)" : "Needs Improvement";

        return AtsAnalysisResponse.builder()
                .overallScore(overall)
                .grade(grade)
                .categoryScores(categoryScores)
                .strongAreas(strongAreas)
                .areasToImprove(areasToImprove)
                .missingInformation(missingInformation)
                .actionableSuggestions(suggestions)
                .disclaimer("ATS score is an automated estimation based on parsing compliance and recruitment heuristics.")
                .build();
    }

    public AiAssistDtos.SummaryGenerateResponse generateSummary(AiAssistDtos.SummaryGenerateRequest request) {
        String role = request.getTargetRole() != null && !request.getTargetRole().isBlank() ? request.getTargetRole() : "Software Professional";
        String exp = request.getYearsOfExperience() != null && !request.getYearsOfExperience().isBlank() ? request.getYearsOfExperience() : "3+";
        String skills = request.getKeySkills() != null && !request.getKeySkills().isEmpty() ? String.join(", ", request.getKeySkills()) : "modern software engineering, architecture, and agile delivery";

        String primary = String.format("Results-driven %s with %s years of proven expertise in %s. Adept at architecting scalable solutions, collaborating across cross-functional teams, and driving measurable engineering velocity and product excellence.", role, exp, skills);
        
        List<String> alternates = List.of(
                String.format("Accomplished %s bringing %s years of hands-on experience in %s. Recognized for delivering robust, high-performance systems and turning complex business requirements into elegant technological solutions.", role, exp, skills),
                String.format("Dynamic and detail-oriented %s specializing in %s with %s years of background in fast-paced software environments. Passionate about continuous optimization and user-centric architecture.", role, skills, exp)
        );

        return AiAssistDtos.SummaryGenerateResponse.builder()
                .summary(primary)
                .alternateSummaries(alternates)
                .build();
    }

    public AiAssistDtos.SectionImproveResponse improveSection(AiAssistDtos.SectionImproveRequest request) {
        String text = request.getCurrentText() != null ? request.getCurrentText() : "";
        List<String> powerVerbs = List.of("Architected", "Engineered", "Optimized", "Spearheaded", "Streamlined", "Delivered");

        String improved;
        List<String> bullets = new ArrayList<>();

        if (text.isBlank()) {
            improved = "Architected and delivered end-to-end features, boosting system performance by 25% and reducing deployment cycles.";
            bullets.add("Architected resilient services handling 100k+ daily transactions with 99.9% uptime.");
            bullets.add("Engineered intuitive user interfaces reducing customer bounce rate by 18%.");
        } else {
            String[] lines = text.split("\n");
            for (String line : lines) {
                String trimmed = line.trim().replaceAll("^[•\\-*]\\s*", "");
                if (!trimmed.isBlank()) {
                    if (!trimmed.endsWith(".")) trimmed += ".";
                    bullets.add("Spearheaded: " + trimmed + " achieving high efficiency and team throughput.");
                }
            }
            if (bullets.isEmpty()) {
                bullets.add("Engineered scalable solutions tailored to high-load environments.");
            }
            improved = String.join("\n", bullets);
        }

        return AiAssistDtos.SectionImproveResponse.builder()
                .improvedText(improved)
                .bulletSuggestions(bullets)
                .powerVerbsUsed(powerVerbs)
                .build();
    }

    public AiAssistDtos.SkillSuggestionResponse suggestSkills(AiAssistDtos.SkillSuggestionRequest request) {
        String role = (request.getTargetRole() != null ? request.getTargetRole() : "").toLowerCase();
        Set<String> existing = request.getExistingSkills() != null ? request.getExistingSkills().stream().map(String::toLowerCase).collect(Collectors.toSet()) : Set.of();

        List<String> tech = new ArrayList<>();
        List<String> soft = List.of("Agile/Scrum", "System Architecture", "Cross-functional Leadership", "Code Review", "Problem Solving");
        List<String> tools = List.of("Docker", "Git/GitHub", "Jira", "CI/CD Actions", "Postman");

        if (role.contains("front") || role.contains("react") || role.contains("ui")) {
            tech.addAll(List.of("React.js", "TypeScript", "Next.js", "Tailwind CSS", "Redux Toolkit", "GraphQL", "Web Vitals", "Jest"));
        } else if (role.contains("java") || role.contains("back") || role.contains("spring")) {
            tech.addAll(List.of("Java 17/21", "Spring Boot", "Microservices", "PostgreSQL", "Kafka", "Redis", "Docker", "RESTful APIs"));
        } else if (role.contains("python") || role.contains("data") || role.contains("ai") || role.contains("machine")) {
            tech.addAll(List.of("Python", "Pandas", "PyTorch", "TensorFlow", "Scikit-Learn", "FastAPI", "SQL", "Airflow"));
        } else if (role.contains("devops") || role.contains("cloud")) {
            tech.addAll(List.of("Kubernetes", "AWS", "Terraform", "CI/CD Pipelines", "Docker", "Prometheus", "Linux/Bash", "Ansible"));
        } else {
            tech.addAll(List.of("JavaScript", "TypeScript", "Node.js", "Python", "SQL", "Cloud Services (AWS/GCP)", "Docker", "Git"));
        }

        List<String> filteredTech = tech.stream().filter(s -> !existing.contains(s.toLowerCase())).collect(Collectors.toList());

        return AiAssistDtos.SkillSuggestionResponse.builder()
                .recommendedTechnicalSkills(filteredTech)
                .recommendedSoftSkills(soft)
                .trendingTools(tools)
                .build();
    }

    public AiAssistDtos.JobAnalysisResponse analyzeJobDescription(String jd) {
        String lower = jd != null ? jd.toLowerCase() : "";

        List<String> allTech = List.of("React", "Java", "Spring Boot", "TypeScript", "Node.js", "Python", "AWS", "Docker", "Kubernetes", "MongoDB", "SQL", "PostgreSQL", "Kafka", "Redis", "GraphQL", "HTML/CSS", "Git", "REST APIs", "Microservices", "CI/CD");
        List<String> matchedReq = allTech.stream().filter(t -> lower.contains(t.toLowerCase())).collect(Collectors.toList());

        List<String> keywords = List.of("Agile", "High-throughput", "Scalability", "Collaboration", "Unit Testing", "Cloud-native", "Performance Optimization");

        return AiAssistDtos.JobAnalysisResponse.builder()
                .jobTitle("Identified Tech Position")
                .requiredSkills(matchedReq.isEmpty() ? List.of("Software Engineering", "Problem Solving", "Git") : matchedReq)
                .preferredSkills(List.of("Cloud Experience (AWS/GCP)", "CI/CD Automation", "System Design"))
                .keyResponsibilities(List.of(
                        "Design and build scalable, production-ready systems.",
                        "Collaborate in agile sprint ceremonies and lead code reviews.",
                        "Optimize system latency, resilience, and automated testing coverage."
                ))
                .importantKeywords(keywords)
                .experienceLevel(lower.contains("senior") ? "Senior Level (5+ yrs)" : lower.contains("junior") ? "Junior Level" : "Mid-Level (2-5 yrs)")
                .build();
    }

    public String formatResumeForLlm(Resume resume) {
        StringBuilder sb = new StringBuilder();
        sb.append("TITLE: ").append(resume.getTitle() != null ? resume.getTitle() : "Untitled Resume").append("\n\n");

        if (resume.getPersonalInfo() != null) {
            PersonalInfo pi = resume.getPersonalInfo();
            sb.append("CANDIDATE INFO:\n");
            if (pi.getFullName() != null) sb.append("- Name: ").append(pi.getFullName()).append("\n");
            if (pi.getEmail() != null) sb.append("- Email: ").append(pi.getEmail()).append("\n");
            if (pi.getLocation() != null) sb.append("- Location: ").append(pi.getLocation()).append("\n");
            if (pi.getLinkedin() != null) sb.append("- LinkedIn: ").append(pi.getLinkedin()).append("\n");
            if (pi.getGithub() != null) sb.append("- GitHub: ").append(pi.getGithub()).append("\n\n");
        }

        if (resume.getSummary() != null && !resume.getSummary().isBlank()) {
            sb.append("PROFESSIONAL SUMMARY:\n").append(resume.getSummary()).append("\n\n");
        }

        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            sb.append("SKILLS: ").append(String.join(", ", resume.getSkills())).append("\n\n");
        }

        if (resume.getExperience() != null && !resume.getExperience().isEmpty()) {
            sb.append("EXPERIENCE:\n");
            for (Experience exp : resume.getExperience()) {
                sb.append("• ").append(exp.getRole()).append(" at ").append(exp.getCompany()).append(" (")
                        .append(exp.getStartDate()).append(" - ").append(exp.isCurrent() ? "Present" : exp.getEndDate()).append(")\n");
                if (exp.getDescription() != null) sb.append("  ").append(exp.getDescription()).append("\n");
            }
            sb.append("\n");
        }

        if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
            sb.append("PROJECTS:\n");
            for (Project p : resume.getProjects()) {
                sb.append("• ").append(p.getTitle()).append(": ").append(p.getDescription() != null ? p.getDescription() : "").append("\n");
            }
            sb.append("\n");
        }

        if (resume.getEducation() != null && !resume.getEducation().isEmpty()) {
            sb.append("EDUCATION:\n");
            for (Education e : resume.getEducation()) {
                sb.append("• ").append(e.getDegree()).append(" from ").append(e.getInstitution()).append("\n");
            }
        }

        return sb.toString();
    }
}
