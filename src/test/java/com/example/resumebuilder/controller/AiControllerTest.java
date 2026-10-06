package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.JwtAuthenticationFilter;
import com.example.resumebuilder.config.JwtTokenProvider;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.service.AiMatchingService;
import com.example.resumebuilder.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AiMatchingService aiMatchingService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void matchResume_Success() throws Exception {
        AiMatchRequest request = AiMatchRequest.builder()
                .resumeId("resume123")
                .jobDescription("Java and Spring Boot developer needed.")
                .build();

        AiMatchResponse response = AiMatchResponse.builder()
                .matchScore(90)
                .matchedSkills(List.of("Java", "Spring Boot"))
                .missingSkills(List.of())
                .aiRecommendations(List.of("Strong fit for the role"))
                .build();

        when(authService.getCurrentUserId()).thenReturn("user123");
        when(aiMatchingService.matchResumeWithJob(any(AiMatchRequest.class), eq("user123"))).thenReturn(response);

        mockMvc.perform(post("/api/ai/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchScore").value(90))
                .andExpect(jsonPath("$.matchedSkills[0]").value("Java"));
    }

    @Test
    void matchResume_MissingFields_ReturnsBadRequest() throws Exception {
        AiMatchRequest request = AiMatchRequest.builder()
                .resumeId("")
                .jobDescription("")
                .build();

        mockMvc.perform(post("/api/ai/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.resumeId").exists())
                .andExpect(jsonPath("$.validationErrors.jobDescription").exists());
    }
}
