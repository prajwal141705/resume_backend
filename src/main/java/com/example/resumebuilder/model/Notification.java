package com.example.resumebuilder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    @Indexed
    private String userId; // Specific user ID or "ADMIN" for all admins

    private String title;

    private String message;

    private String type; // RESUME_CREATED, ATS_ANALYSIS, TEMPLATE_APPROVED, TEMPLATE_SUBMITTED, JOB_MATCH, SYSTEM

    private String link;

    @Builder.Default
    private boolean read = false;

    @CreatedDate
    private Instant createdAt;
}
