package com.example.resumebuilder.service;

import com.example.resumebuilder.model.dto.AiMatchResponse;

public interface LlmService {
    AiMatchResponse analyzeResumeAgainstJob(String resumeText, String jobDescription);
}
