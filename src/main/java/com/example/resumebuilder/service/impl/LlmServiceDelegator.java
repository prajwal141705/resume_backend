package com.example.resumebuilder.service.impl;

import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service("llmService")
public class LlmServiceDelegator implements LlmService {

    private final LlmService geminiLlmService;
    private final LlmService openAiLlmService;

    @Value("${ai.provider:gemini}")
    private String aiProvider;

    public LlmServiceDelegator(
            @Qualifier("geminiLlmService") LlmService geminiLlmService,
            @Qualifier("openAiLlmService") LlmService openAiLlmService) {
        this.geminiLlmService = geminiLlmService;
        this.openAiLlmService = openAiLlmService;
    }

    @Override
    public AiMatchResponse analyzeResumeAgainstJob(String resumeText, String jobDescription) {
        log.info("Analyzing resume with AI provider: {}", aiProvider);
        if ("openai".equalsIgnoreCase(aiProvider.trim())) {
            return openAiLlmService.analyzeResumeAgainstJob(resumeText, jobDescription);
        } else {
            return geminiLlmService.analyzeResumeAgainstJob(resumeText, jobDescription);
        }
    }
}
