package com.example.resumebuilder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resumes")
public class Resume {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    @Builder.Default
    private String template = "modern"; // modern, classic, professional, minimal, creative, developer, corporate, executive, ats-friendly, two-column

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
    private String accentColor = "#4f46e5"; // Indigo default
    @Builder.Default
    private String fontFamily = "Inter, sans-serif";
    @Builder.Default
    private String fontSize = "medium"; // small, medium, large
    @Builder.Default
    private String spacing = "normal"; // compact, normal, relaxed
    @Builder.Default
    private String margins = "normal"; // tight, normal, wide
    @Builder.Default
    private String layout = "one-column"; // one-column, two-column

    @Builder.Default
    private List<String> sectionOrder = new ArrayList<>();

    @Builder.Default
    private int atsScore = 0;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, ARCHIVED, DRAFT

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
