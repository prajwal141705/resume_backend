package com.example.resumebuilder.model.dto;

import com.example.resumebuilder.model.Certification;
import com.example.resumebuilder.model.CustomSection;
import com.example.resumebuilder.model.Education;
import com.example.resumebuilder.model.Experience;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {
    private String id;
    private String userId;
    private String title;
    
    @Builder.Default
    private String template = "modern";
    
    private PersonalInfo personalInfo;
    private String summary;
    private String careerObjective;

    @Builder.Default
    private List<Education> education = new ArrayList<>();

    @Builder.Default
    private List<Experience> experience = new ArrayList<>();

    @Builder.Default
    private List<Experience> internships = new ArrayList<>();

    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Builder.Default
    private List<String> technicalSkills = new ArrayList<>();

    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    @Builder.Default
    private List<String> achievements = new ArrayList<>();

    @Builder.Default
    private List<String> languages = new ArrayList<>();

    @Builder.Default
    private List<String> hobbies = new ArrayList<>();

    @Builder.Default
    private List<CustomSection> customSections = new ArrayList<>();

    // Customization Settings
    @Builder.Default
    private String accentColor = "#4f46e5";
    @Builder.Default
    private String fontFamily = "Inter, sans-serif";
    @Builder.Default
    private String fontSize = "medium";
    @Builder.Default
    private String spacing = "normal";
    @Builder.Default
    private String margins = "normal";
    @Builder.Default
    private String layout = "one-column";

    @Builder.Default
    private List<String> sectionOrder = new ArrayList<>();

    @Builder.Default
    private int atsScore = 0;

    @Builder.Default
    private String status = "ACTIVE";

    private Instant createdAt;
    private Instant updatedAt;
}
