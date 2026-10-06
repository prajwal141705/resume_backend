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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "jobs")
public class Job {

    @Id
    private String id;

    private String title;

    private String company;

    private String description;

    @Builder.Default
    private List<String> requiredSkills = new ArrayList<>();

    @Builder.Default
    private String experienceLevel = "Mid-Level"; // Entry-Level, Junior, Mid-Level, Senior, Lead

    private String location;

    @Builder.Default
    private String jobType = "Full-time"; // Full-time, Part-time, Remote, Contract

    private String salaryRange;

    @Builder.Default
    private boolean enabled = true;

    private String createdBy;

    @Indexed
    @CreatedDate
    private Instant postedDate;

    @LastModifiedDate
    private Instant updatedAt;
}
