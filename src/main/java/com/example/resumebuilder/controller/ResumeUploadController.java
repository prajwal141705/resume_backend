package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.UploadedResume;
import com.example.resumebuilder.model.dto.ResumeUploadResponse;
import com.example.resumebuilder.service.AuthService;
import com.example.resumebuilder.service.ResumeUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/upload-resume")
@RequiredArgsConstructor
public class ResumeUploadController {

    private final ResumeUploadService uploadService;
    private final AuthService authService;

    @PostMapping(value = "/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeUploadResponse> uploadPdfResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        String userEmail = currentUser != null ? currentUser.getEmail() : "user@example.com";

        log.info("PDF upload request received for user: {}", userId);
        ResumeUploadResponse response = uploadService.processPdfUpload(file, userId, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<UploadedResume>> getUploadHistory(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        return ResponseEntity.ok(uploadService.getUserUploads(userId));
    }
}
