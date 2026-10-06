package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.JwtAuthenticationFilter;
import com.example.resumebuilder.config.JwtTokenProvider;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.dto.ResumeRequest;
import com.example.resumebuilder.model.dto.ResumeResponse;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.ResumeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResumeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void createResume_Success() throws Exception {
        ResumeRequest request = ResumeRequest.builder()
                .title("Software Engineer")
                .personalInfo(PersonalInfo.builder()
                        .fullName("John Doe")
                        .email("john@example.com")
                        .build())
                .build();

        ResumeResponse response = ResumeResponse.builder()
                .id("resume123")
                .userId("user123")
                .title("Software Engineer")
                .build();

        when(authService.getCurrentUserId()).thenReturn("user123");
        when(resumeService.createResume(any(ResumeRequest.class), eq("user123"))).thenReturn(response);

        mockMvc.perform(post("/api/resumes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("resume123"))
                .andExpect(jsonPath("$.title").value("Software Engineer"));
    }

    @Test
    void getResumeById_Success() throws Exception {
        ResumeResponse response = ResumeResponse.builder()
                .id("resume123")
                .userId("user123")
                .title("Software Engineer")
                .build();

        when(authService.getCurrentUserId()).thenReturn("user123");
        when(resumeService.getResumeById("resume123", "user123")).thenReturn(response);

        mockMvc.perform(get("/api/resumes/resume123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("resume123"));
    }

    @Test
    void getResumesByUserId_Success() throws Exception {
        ResumeResponse response = ResumeResponse.builder()
                .id("resume123")
                .userId("user123")
                .title("Software Engineer")
                .build();

        when(authService.getCurrentUserId()).thenReturn("user123");
        when(resumeService.getResumesByUserId("user123", "user123")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/resumes/user/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("resume123"));
    }

    @Test
    void updateResume_Success() throws Exception {
        ResumeRequest request = ResumeRequest.builder()
                .title("Updated Title")
                .personalInfo(PersonalInfo.builder()
                        .fullName("John Doe")
                        .email("john@example.com")
                        .build())
                .build();

        ResumeResponse response = ResumeResponse.builder()
                .id("resume123")
                .userId("user123")
                .title("Updated Title")
                .build();

        when(authService.getCurrentUserId()).thenReturn("user123");
        when(resumeService.updateResume(eq("resume123"), any(ResumeRequest.class), eq("user123"))).thenReturn(response);

        mockMvc.perform(put("/api/resumes/resume123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteResume_Success() throws Exception {
        when(authService.getCurrentUserId()).thenReturn("user123");

        mockMvc.perform(delete("/api/resumes/resume123"))
                .andExpect(status().isNoContent());
    }
}
