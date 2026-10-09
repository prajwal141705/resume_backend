package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.ResumeVersion;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getCurrentUserResumes(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("Fetching all resumes for current user: {}", currentUserId);
        List<ResumeResponse> resumes = resumeService.getResumesByUserId(currentUserId, currentUserId);
        return ResponseEntity.ok(resumes);
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

    // --- Version History Endpoints ---
    @GetMapping("/{resumeId}/versions")
    public ResponseEntity<List<ResumeVersion>> getResumeVersions(
            @PathVariable String resumeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        return ResponseEntity.ok(resumeService.getResumeVersions(resumeId, currentUserId));
    }

    @PostMapping("/{resumeId}/versions")
    public ResponseEntity<ResumeVersion> createManualVersion(
            @PathVariable String resumeId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        String versionName = body.getOrDefault("versionName", "Manual Snapshot");
        String notes = body.getOrDefault("notes", "");
        ResumeVersion version = resumeService.createManualVersion(resumeId, versionName, notes, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(version);
    }

    @PostMapping("/{resumeId}/versions/{versionId}/restore")
    public ResponseEntity<ResumeResponse> restoreVersion(
            @PathVariable String resumeId,
            @PathVariable String versionId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String currentUserId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        ResumeResponse response = resumeService.restoreResumeVersion(resumeId, versionId, currentUserId);
        return ResponseEntity.ok(response);
    }
}
