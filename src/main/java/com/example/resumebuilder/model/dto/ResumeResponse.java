package com.example.resumebuilder.model.dto;

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
    private PersonalInfo personalInfo;
    private String summary;

    @Builder.Default
    private List<Education> education = new ArrayList<>();

    @Builder.Default
    private List<Experience> experience = new ArrayList<>();

    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
