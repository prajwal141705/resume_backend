package com.example.resumebuilder.config;

import com.example.resumebuilder.model.Job;
import com.example.resumebuilder.model.ResumeTemplate;
import com.example.resumebuilder.model.User;
import com.example.resumebuilder.repository.JobRepository;
import com.example.resumebuilder.repository.ResumeTemplateRepository;
import com.example.resumebuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResumeTemplateRepository templateRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initTemplates();
        initJobs();
    }

    private void initUsers() {
        // Remove old dummy seed accounts if they exist
        userRepository.findByEmail("admin@resumebuilder.com").ifPresent(userRepository::delete);
        userRepository.findByEmail("user@resumebuilder.com").ifPresent(userRepository::delete);

        // Seed / Update configured Admin Account
        String adminEmail = "prajwal@gmail.com";
        Optional<User> existingAdmin = userRepository.findByEmail(adminEmail);

        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            admin.setPasswordHash(passwordEncoder.encode("prajwal1706"));
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
            admin.setStatus("ACTIVE");
            admin.setUpdatedAt(Instant.now());
            userRepository.save(admin);
            log.info("Updated Admin account credentials: {}", adminEmail);
        } else {
            User admin = User.builder()
                    .name("Prajwal Admin")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("prajwal1706"))
                    .roles(Set.of("ROLE_ADMIN", "ROLE_USER"))
                    .status("ACTIVE")
                    .phone("+91 98765 43210")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            userRepository.save(admin);
            log.info("Initialized Admin account: {}", adminEmail);
        }
    }

    private void initTemplates() {
        List<ResumeTemplate> initialTemplates = List.of(
                ResumeTemplate.builder()
                        .slug("modern")
                        .name("Modern Tech")
                        .description("Sleek, modern layout with vibrant accents and optimal white space.")
                        .category("Standard")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("classic")
                        .name("Classic Traditional")
                        .description("Timeless serif typography with balanced margins. Ideal for law, finance, and academia.")
                        .category("Standard")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("professional")
                        .name("Professional Corporate")
                        .description("Structured grid with clear horizontal dividers and strong hierarchical headings.")
                        .category("Corporate")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("minimal")
                        .name("Minimalist Clean")
                        .description("Ultra clean, distraction-free layout emphasizing key achievements and readable metrics.")
                        .category("Standard")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("creative")
                        .name("Creative Portfolio")
                        .description("Dynamic header with bold branding and stylized skill badges for designers & frontend devs.")
                        .category("Creative")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("developer")
                        .name("Developer Monospace")
                        .description("Code-inspired accents, git icons, tech chips, and monospace highlights for software engineers.")
                        .category("Tech")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("corporate")
                        .name("Corporate Blue")
                        .description("Executive navy header with clean sections tailored for corporate and management roles.")
                        .category("Corporate")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("executive")
                        .name("Executive Leadership")
                        .description("Sophisticated slate headers and highlighted career milestones for directors and managers.")
                        .category("Executive")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("ats-friendly")
                        .name("ATS Optimized")
                        .description("High-parseability single column layout engineered specifically to pass recruiter robot filters.")
                        .category("ATS")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("two-column")
                        .name("Two Column Compact")
                        .description("Dual-column efficiency placing contact and skills on the left with experience on the right.")
                        .category("Standard")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build()
        );

        for (ResumeTemplate template : initialTemplates) {
            if (!templateRepository.existsBySlug(template.getSlug())) {
                templateRepository.save(template);
            }
        }
        log.info("Initialized {} resume templates", initialTemplates.size());
    }

    private void initJobs() {
        if (jobRepository.count() == 0) {
            List<Job> jobs = List.of(
                    Job.builder()
                            .title("Senior Full Stack Software Engineer")
                            .company("TechCorp Global")
                            .description("Lead the development of scalable cloud web applications using React, Spring Boot, and MongoDB.")
                            .requiredSkills(List.of("Java", "Spring Boot", "React", "MongoDB", "Docker", "REST API", "Git"))
                            .experienceLevel("Senior")
                            .location("San Francisco, CA (Hybrid)")
                            .jobType("Full-time")
                            .salaryRange("$140,000 - $175,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Frontend Developer (React / Next.js)")
                            .company("PixelCraft Digital")
                            .description("Build pixel-perfect, accessible, and high-performance user interfaces with React and Tailwind CSS.")
                            .requiredSkills(List.of("React", "JavaScript", "TypeScript", "Tailwind CSS", "HTML", "CSS", "Git"))
                            .experienceLevel("Mid-Level")
                            .location("Remote")
                            .jobType("Full-time")
                            .salaryRange("$95,000 - $125,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Backend Java & Cloud Engineer")
                            .company("Nexus FinTech")
                            .description("Design high-throughput microservices, transactional architectures, and Kafka event pipelines.")
                            .requiredSkills(List.of("Java", "Spring Boot", "Microservices", "PostgreSQL", "Kafka", "AWS", "Docker"))
                            .experienceLevel("Senior")
                            .location("New York, NY")
                            .jobType("Full-time")
                            .salaryRange("$130,000 - $160,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("DevOps & Cloud Infrastructure Engineer")
                            .company("CloudScale Systems")
                            .description("Automate CI/CD pipelines, manage Kubernetes clusters, and maintain 99.99% uptime on AWS infrastructure.")
                            .requiredSkills(List.of("AWS", "Docker", "Kubernetes", "CI/CD", "Terraform", "Linux", "Python"))
                            .experienceLevel("Mid-Level")
                            .location("Austin, TX (Remote)")
                            .jobType("Full-time")
                            .salaryRange("$110,000 - $145,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build()
            );

            jobRepository.saveAll(jobs);
            log.info("Initialized default sample jobs");
        }
    }
}
