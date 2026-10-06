package com.example.resumebuilder.service;

import com.example.resumebuilder.exception.ResourceNotFoundException;
import com.example.resumebuilder.model.ResumeTemplate;
import com.example.resumebuilder.model.UserResumeTemplateRequest;
import com.example.resumebuilder.model.dto.TemplateRequestDto;
import com.example.resumebuilder.repository.ResumeTemplateRepository;
import com.example.resumebuilder.repository.UserResumeTemplateRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeTemplateService {

    private final ResumeTemplateRepository templateRepository;
    private final UserResumeTemplateRequestRepository templateRequestRepository;

    public List<ResumeTemplate> getActiveTemplates() {
        return templateRepository.findByEnabledTrueOrderByCreatedAtAsc();
    }

    public List<ResumeTemplate> getAllTemplates() {
        return templateRepository.findAllByOrderByCreatedAtAsc();
    }

    public ResumeTemplate getTemplateBySlug(String slug) {
        return templateRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with slug: " + slug));
    }

    public UserResumeTemplateRequest submitTemplateRequest(TemplateRequestDto dto, String userId, String userEmail, String userName) {
        log.info("User {} submitting custom template request: {}", userId, dto.getTemplateName());

        UserResumeTemplateRequest request = UserResumeTemplateRequest.builder()
                .userId(userId)
                .userEmail(userEmail)
                .userName(userName)
                .templateName(dto.getTemplateName())
                .description(dto.getDescription())
                .previewUrl(dto.getPreviewUrl())
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        return templateRequestRepository.save(request);
    }

    public List<UserResumeTemplateRequest> getUserTemplateRequests(String userId) {
        return templateRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
