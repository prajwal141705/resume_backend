package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.JwtAuthenticationFilter;
import com.example.resumebuilder.config.JwtTokenProvider;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.JobResponse;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.JobService;
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

@WebMvcTest(JobController.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void createJob_Success() throws Exception {
        JobRequest request = JobRequest.builder()
                .title("Full Stack Developer")
                .company("Tech Innovations")
                .description("Build awesome apps")
                .requiredSkills(List.of("Java", "React"))
                .build();

        JobResponse response = JobResponse.builder()
                .id("job123")
                .title("Full Stack Developer")
                .company("Tech Innovations")
                .build();

        when(authService.getCurrentUserId()).thenReturn("recruiter123");
        when(jobService.createJob(any(JobRequest.class), eq("recruiter123"))).thenReturn(response);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("job123"))
                .andExpect(jsonPath("$.company").value("Tech Innovations"));
    }

    @Test
    void getAllJobs_Success() throws Exception {
        JobResponse response = JobResponse.builder()
                .id("job123")
                .title("Full Stack Developer")
                .company("Tech Innovations")
                .build();

        when(jobService.getAllJobs()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("job123"));
    }

    @Test
    void getJobById_Success() throws Exception {
        JobResponse response = JobResponse.builder()
                .id("job123")
                .title("Full Stack Developer")
                .build();

        when(jobService.getJobById("job123")).thenReturn(response);

        mockMvc.perform(get("/api/jobs/job123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("job123"));
    }

    @Test
    void updateJob_Success() throws Exception {
        JobRequest request = JobRequest.builder()
                .title("Senior Full Stack Developer")
                .company("Tech Innovations")
                .description("Updated description")
                .build();

        JobResponse response = JobResponse.builder()
                .id("job123")
                .title("Senior Full Stack Developer")
                .build();

        when(authService.getCurrentUserId()).thenReturn("recruiter123");
        when(jobService.updateJob(eq("job123"), any(JobRequest.class), eq("recruiter123"))).thenReturn(response);

        mockMvc.perform(put("/api/jobs/job123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Full Stack Developer"));
    }

    @Test
    void deleteJob_Success() throws Exception {
        when(authService.getCurrentUserId()).thenReturn("recruiter123");

        mockMvc.perform(delete("/api/jobs/job123"))
                .andExpect(status().isNoContent());
    }
}
