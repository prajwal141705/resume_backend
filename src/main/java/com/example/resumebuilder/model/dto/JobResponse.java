package com.example.resumebuilder.model.dto;

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
public class JobResponse {
    private String id;
    private String title;
    private String company;
    private String description;

    @Builder.Default
    private List<String> requiredSkills = new ArrayList<>();

    private String location;
    private String jobType;
    private String salaryRange;
    private String createdBy;
    private Instant postedDate;
    private Instant updatedAt;
}
