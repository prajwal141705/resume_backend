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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resume_templates")
public class ResumeTemplate {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug; // modern, classic, professional, minimal, creative, developer, corporate, executive, ats-friendly, two-column

    private String name;

    private String description;

    @Builder.Default
    private String category = "Standard"; // Standard, Tech, Creative, Executive, ATS

    @Builder.Default
    private boolean enabled = true;

    private String previewUrl;

    @Builder.Default
    private boolean isCustom = false;

    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
