package com.example.resumebuilder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadResponse {
    private String uploadedResumeId;
    private String fileName;
    private String extractedText;
    private ResumeResponse parsedResume;
    private String message;
}
