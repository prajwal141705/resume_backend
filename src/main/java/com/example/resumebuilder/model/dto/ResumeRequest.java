package com.example.resumebuilder.model.dto;

import com.example.resumebuilder.model.Education;
import com.example.resumebuilder.model.Experience;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Project;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Personal info is required")
    @Valid
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
}
