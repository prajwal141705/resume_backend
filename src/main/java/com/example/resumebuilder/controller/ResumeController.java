package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.dto.ResumeRequest;
import com.example.resumebuilder.model.dto.ResumeResponse;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ResumeResponse> createResume(
            @Valid @RequestBody ResumeRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Creating resume for user: {}", userId);
        ResumeResponse response = resumeService.createResume(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ResumeResponse>> getResumesByUserId(
            @PathVariable String userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Fetching resumes for user: {} by requester: {}", userId, currentUserId);
        List<ResumeResponse> resumes = resumeService.getResumesByUserId(userId, currentUserId);
        return ResponseEntity.ok(resumes);
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> getResumeById(
            @PathVariable String resumeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Fetching resume: {} by user: {}", resumeId, currentUserId);
        ResumeResponse response = resumeService.getResumeById(resumeId, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{resumeId}")
    public ResponseEntity<ResumeResponse> updateResume(
            @PathVariable String resumeId,
            @Valid @RequestBody ResumeRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Updating resume: {} by user: {}", resumeId, currentUserId);
        ResumeResponse response = resumeService.updateResume(resumeId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{resumeId}/duplicate")
    public ResponseEntity<ResumeResponse> duplicateResume(
            @PathVariable String resumeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Duplicating resume: {} by user: {}", resumeId, currentUserId);
        ResumeResponse response = resumeService.duplicateResume(resumeId, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable String resumeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Deleting resume: {} by user: {}", resumeId, currentUserId);
        resumeService.deleteResume(resumeId, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
