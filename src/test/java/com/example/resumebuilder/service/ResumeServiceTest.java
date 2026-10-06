package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.PersonalInfo;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.ResumeRequest;
import com.example.resumebuilder.model.dto.ResumeResponse;
import com.example.resumebuilder.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private ResumeService resumeService;

    private Resume sampleResume;
    private PersonalInfo samplePersonalInfo;

    @BeforeEach
    void setUp() {
        samplePersonalInfo = PersonalInfo.builder()
                .fullName("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .location("New York")
                .build();

        sampleResume = Resume.builder()
                .id("resume123")
                .userId("user123")
                .title("Full Stack Developer")
                .personalInfo(samplePersonalInfo)
                .summary("Experienced software engineer")
                .skills(List.of("Java", "Spring Boot", "React"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createResume_Success() {
        ResumeRequest request = ResumeRequest.builder()
                .title("Full Stack Developer")
                .personalInfo(samplePersonalInfo)
                .summary("Experienced software engineer")
                .skills(List.of("Java", "Spring Boot", "React"))
                .build();

        when(resumeRepository.save(any(Resume.class))).thenReturn(sampleResume);

        ResumeResponse response = resumeService.createResume(request, "user123");

        assertNotNull(response);
        assertEquals("resume123", response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals("Full Stack Developer", response.getTitle());
    }

    @Test
    void getResumesByUserId_Authorized_Success() {
        when(resumeRepository.findByUserId("user123")).thenReturn(List.of(sampleResume));

        List<ResumeResponse> results = resumeService.getResumesByUserId("user123", "user123");

        assertEquals(1, results.size());
        assertEquals("resume123", results.get(0).getId());
    }

    @Test
    void getResumesByUserId_UnauthorizedUser_ThrowsForbiddenException() {
        assertThrows(ForbiddenException.class, () ->
                resumeService.getResumesByUserId("user456", "user123"));
    }

    @Test
    void getResumeById_Owner_Success() {
        when(resumeRepository.findById("resume123")).thenReturn(Optional.of(sampleResume));

        ResumeResponse response = resumeService.getResumeById("resume123", "user123");

        assertNotNull(response);
        assertEquals("resume123", response.getId());
    }

    @Test
    void getResumeById_NotOwner_ThrowsForbiddenException() {
        when(resumeRepository.findById("resume123")).thenReturn(Optional.of(sampleResume));

        assertThrows(ForbiddenException.class, () ->
                resumeService.getResumeById("resume123", "otherUser456"));
    }

    @Test
    void getResumeById_NotFound_ThrowsResourceNotFoundException() {
        when(resumeRepository.findById("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                resumeService.getResumeById("nonExistentId", "user123"));
    }

    @Test
    void updateResume_Owner_Success() {
        ResumeRequest updateRequest = ResumeRequest.builder()
                .title("Senior Software Engineer")
                .personalInfo(samplePersonalInfo)
                .summary("Updated summary")
                .skills(List.of("Java", "Spring Boot", "Docker"))
                .build();

        when(resumeRepository.findById("resume123")).thenReturn(Optional.of(sampleResume));
        when(resumeRepository.save(any(Resume.class))).thenReturn(sampleResume);

        ResumeResponse response = resumeService.updateResume("resume123", updateRequest, "user123");

        assertNotNull(response);
        verify(resumeRepository).save(sampleResume);
    }

    @Test
    void deleteResume_Owner_Success() {
        when(resumeRepository.findById("resume123")).thenReturn(Optional.of(sampleResume));

        resumeService.deleteResume("resume123", "user123");

        verify(resumeRepository).delete(sampleResume);
    }
}
