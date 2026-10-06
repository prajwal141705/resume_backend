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
@Document(collection = "uploaded_resumes")
public class UploadedResume {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String originalFileName;

    private long fileSizeBytes;

    private String contentType;

    private String extractedText;

    private String parsedResumeId;

    @Builder.Default
    private String status = "PARSED"; // PARSED, FAILED

    private String userEmail;

    @CreatedDate
    private Instant createdAt;
}
