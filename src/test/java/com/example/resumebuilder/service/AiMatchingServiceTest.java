package com.example.resumebuilder.service;

import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiMatchingServiceTest {

    @Mock
    private ResumeService resumeService;

    @Mock
    private LlmService llmService;

    @InjectMocks
    private AiMatchingService aiMatchingService;

    private Resume sampleResume;

    @BeforeEach
    void setUp() {
        sampleResume = Resume.builder()
                .id("resume123")
                .userId("user123")
                .title("Full Stack Java Engineer")
                .personalInfo(PersonalInfo.builder()
                        .fullName("John Doe")
                        .email("john@example.com")
                        .location("San Francisco, CA")
                        .build())
                .summary("5 years of experience building Spring Boot applications")
                .skills(List.of("Java", "Spring Boot", "React", "MongoDB"))
                .build();
    }

    @Test
    void matchResumeWithJob_Success() {
        AiMatchRequest request = AiMatchRequest.builder()
                .resumeId("resume123")
                .jobDescription("Looking for a Senior Java Developer with Spring Boot and AWS experience.")
                .build();

        AiMatchResponse expectedResponse = AiMatchResponse.builder()
                .matchScore(85)
                .matchedSkills(List.of("Java", "Spring Boot"))
                .missingSkills(List.of("AWS"))
                .aiRecommendations(List.of("Highlight cloud deployment experience"))
                .build();

        when(resumeService.getResumeEntityById("resume123", "user123")).thenReturn(sampleResume);
        when(llmService.analyzeResumeAgainstJob(anyString(), anyString())).thenReturn(expectedResponse);

        AiMatchResponse result = aiMatchingService.matchResumeWithJob(request, "user123");

        assertNotNull(result);
        assertEquals(85, result.getMatchScore());
        assertEquals(2, result.getMatchedSkills().size());
        assertEquals(1, result.getMissingSkills().size());
        verify(resumeService).getResumeEntityById("resume123", "user123");
        verify(llmService).analyzeResumeAgainstJob(anyString(), anyString());
    }

    @Test
    void formatResumeForLlm_ContainsEssentialInformation() {
        String formatted = aiMatchingService.formatResumeForLlm(sampleResume);

        assertTrue(formatted.contains("Full Stack Java Engineer"));
        assertTrue(formatted.contains("John Doe"));
        assertTrue(formatted.contains("john@example.com"));
        assertTrue(formatted.contains("Spring Boot"));
    }
}
