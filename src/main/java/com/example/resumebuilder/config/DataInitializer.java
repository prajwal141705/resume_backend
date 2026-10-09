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
                        .category("Modern")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("classic")
                        .name("Classic Traditional")
                        .description("Timeless serif typography with balanced margins. Ideal for law, finance, and academia.")
                        .category("Classic")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("professional")
                        .name("Professional Corporate")
                        .description("Structured grid with clear horizontal dividers and strong hierarchical headings.")
                        .category("Professional")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("minimal")
                        .name("Minimalist Clean")
                        .description("Ultra clean, distraction-free layout emphasizing key achievements and readable metrics.")
                        .category("Minimal")
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
                        .category("Developer")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("corporate")
                        .name("Corporate Navy")
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
                        .category("ATS Friendly")
                        .enabled(true)
                        .isCustom(false)
                        .createdAt(Instant.now())
                        .build(),
                ResumeTemplate.builder()
                        .slug("two-column")
                        .name("Two Column Compact")
                        .description("Dual-column efficiency placing contact and skills on the left with experience on the right.")
                        .category("Two Column")
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
                            .title("Frontend Developer")
                            .company("Apex Web Studio")
                            .description("Build responsive, accessible web applications with modern HTML, CSS, JavaScript, and Vue/React.")
                            .requiredSkills(List.of("JavaScript", "HTML", "CSS", "Responsive Design", "Git", "REST APIs"))
                            .experienceLevel("Mid-Level")
                            .location("Remote")
                            .jobType("Full-time")
                            .salaryRange("$85,000 - $115,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("React Developer")
                            .company("PixelCraft Digital")
                            .description("Build pixel-perfect, accessible, and high-performance user interfaces with React, Next.js, and Tailwind CSS.")
                            .requiredSkills(List.of("React", "TypeScript", "Next.js", "Tailwind CSS", "Redux", "Jest", "Git"))
                            .experienceLevel("Mid-Level")
                            .location("San Francisco, CA (Hybrid)")
                            .jobType("Full-time")
                            .salaryRange("$105,000 - $135,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Backend Developer")
                            .company("CloudForge Technologies")
                            .description("Design and scale REST and GraphQL microservices, relational and NoSQL databases, and API gateways.")
                            .requiredSkills(List.of("Node.js", "Express", "PostgreSQL", "MongoDB", "Redis", "Docker", "REST API"))
                            .experienceLevel("Mid-Level")
                            .location("Austin, TX")
                            .jobType("Full-time")
                            .salaryRange("$110,000 - $140,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Java Developer")
                            .company("Nexus FinTech Solutions")
                            .description("Design high-throughput microservices, secure transaction pipelines, and event-driven architectures with Spring Boot.")
                            .requiredSkills(List.of("Java", "Spring Boot", "Microservices", "Kafka", "PostgreSQL", "Docker", "JUnit"))
                            .experienceLevel("Senior")
                            .location("New York, NY")
                            .jobType("Full-time")
                            .salaryRange("$130,000 - $165,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Full Stack Developer")
                            .company("Global Scale Interactive")
                            .description("End-to-end development of client-facing platforms using React, Node.js/Spring, Docker, and AWS.")
                            .requiredSkills(List.of("React", "Java", "Spring Boot", "MongoDB", "TypeScript", "AWS", "Docker", "Git"))
                            .experienceLevel("Senior")
                            .location("Seattle, WA (Hybrid)")
                            .jobType("Full-time")
                            .salaryRange("$140,000 - $180,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Python Developer")
                            .company("DataCore Analytics")
                            .description("Develop high-speed data pipelines, backend APIs using FastAPI/Django, and asynchronous background workers.")
                            .requiredSkills(List.of("Python", "FastAPI", "Django", "PostgreSQL", "Celery", "Redis", "Docker", "Git"))
                            .experienceLevel("Mid-Level")
                            .location("Chicago, IL (Remote)")
                            .jobType("Full-time")
                            .salaryRange("$100,000 - $130,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Data Scientist")
                            .company("Insight AI Labs")
                            .description("Build predictive ML models, NLP classification pipelines, and feature engineering workflows in production.")
                            .requiredSkills(List.of("Python", "Pandas", "Scikit-Learn", "PyTorch", "SQL", "Machine Learning", "Data Visualization"))
                            .experienceLevel("Mid-Level")
                            .location("Boston, MA")
                            .jobType("Full-time")
                            .salaryRange("$125,000 - $160,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("UI/UX Designer")
                            .company("Lumina Creative Agency")
                            .description("Craft wireframes, interactive Figma design systems, user journey maps, and high-fidelity product prototypes.")
                            .requiredSkills(List.of("Figma", "UI/UX Design", "Wireframing", "Prototyping", "Design Systems", "User Research"))
                            .experienceLevel("Mid-Level")
                            .location("Remote")
                            .jobType("Full-time")
                            .salaryRange("$90,000 - $120,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("DevOps Engineer")
                            .company("CloudSphere Ops")
                            .description("Maintain Kubernetes clusters, automate multi-region Terraform infrastructure, and harden CI/CD pipelines.")
                            .requiredSkills(List.of("AWS", "Kubernetes", "Docker", "Terraform", "CI/CD", "Linux", "Python", "Monitoring"))
                            .experienceLevel("Senior")
                            .location("Denver, CO (Remote)")
                            .jobType("Full-time")
                            .salaryRange("$135,000 - $170,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build(),
                    Job.builder()
                            .title("Software Engineer")
                            .company("OmniTech Ventures")
                            .description("Design, code, and optimize core business services, scalable distributed systems, and collaborative developer tooling.")
                            .requiredSkills(List.of("Java", "Python", "Algorithms", "Data Structures", "System Design", "SQL", "Git"))
                            .experienceLevel("Entry-Level / Mid")
                            .location("San Jose, CA")
                            .jobType("Full-time")
                            .salaryRange("$115,000 - $150,000")
                            .enabled(true)
                            .postedDate(Instant.now())
                            .build()
            );

            jobRepository.saveAll(jobs);
            log.info("Initialized default 10 sample tech jobs");
        }
    }
}
