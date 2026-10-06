package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.ResumeTemplate;
import com.example.resumebuilder.model.UserResumeTemplateRequest;
import com.example.resumebuilder.model.dto.TemplateRequestDto;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.ResumeTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class ResumeTemplateController {

    private final ResumeTemplateService templateService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<ResumeTemplate>> getActiveTemplates() {
        return ResponseEntity.ok(templateService.getActiveTemplates());
    }

    @PostMapping("/request")
    public ResponseEntity<UserResumeTemplateRequest> submitTemplateRequest(
            @Valid @RequestBody TemplateRequestDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        String userEmail = currentUser != null ? currentUser.getEmail() : "user@example.com";
        String userName = currentUser != null ? currentUser.getName() : "User";

        UserResumeTemplateRequest created = templateService.submitTemplateRequest(dto, userId, userEmail, userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<UserResumeTemplateRequest>> getMyTemplateRequests(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        return ResponseEntity.ok(templateService.getUserTemplateRequests(userId));
    }
}
