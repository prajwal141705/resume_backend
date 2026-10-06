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
@Document(collection = "user_template_requests")
public class UserResumeTemplateRequest {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String userEmail;

    private String userName;

    private String templateName;

    private String description;

    private String previewUrl;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String adminFeedback;

    private String reviewedBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant reviewedAt;
}
