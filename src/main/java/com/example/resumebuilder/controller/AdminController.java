package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.Job;
import com.example.resumebuilder.model.ResumeTemplate;
import com.example.resumebuilder.model.UploadedResume;
import com.example.resumebuilder.model.UserResumeTemplateRequest;
import com.example.resumebuilder.model.dto.AdminStatsResponse;
import com.example.resumebuilder.model.dto.JobRequest;
import com.example.resumebuilder.model.dto.TemplateStatusUpdateRequest;
import com.example.resumebuilder.model.dto.UserDto;
import com.example.resumebuilder.model.dto.UserStatusUpdateRequest;
import com.example.resumebuilder.service.AdminService;
import com.example.resumebuilder.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    // --- Dashboard Stats ---
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    // --- User Management ---
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllUsers(search, status));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable String id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, request.getStatus()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --- Job Management ---
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String adminId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        Job job = adminService.createJob(request, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable String id,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(adminService.updateJob(id, request));
    }

    @PutMapping("/jobs/{id}/toggle-status")
    public ResponseEntity<Job> toggleJobStatus(@PathVariable String id) {
        return ResponseEntity.ok(adminService.toggleJobStatus(id));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
        adminService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // --- Template Management ---
    @GetMapping("/templates")
    public ResponseEntity<List<ResumeTemplate>> getAllTemplates() {
        return ResponseEntity.ok(adminService.getAllTemplates());
    }

    @PostMapping("/templates")
    public ResponseEntity<ResumeTemplate> createTemplate(
            @RequestBody ResumeTemplate template,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String adminId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        ResumeTemplate created = adminService.createTemplate(template, adminId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<ResumeTemplate> updateTemplate(
            @PathVariable String id,
            @RequestBody ResumeTemplate template) {
        return ResponseEntity.ok(adminService.updateTemplate(id, template));
    }

    @PutMapping("/templates/{id}/toggle-status")
    public ResponseEntity<ResumeTemplate> toggleTemplateStatus(@PathVariable String id) {
        return ResponseEntity.ok(adminService.toggleTemplateStatus(id));
    }

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        adminService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    // --- Template Requests Review ---
    @GetMapping("/template-requests")
    public ResponseEntity<List<UserResumeTemplateRequest>> getAllTemplateRequests(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllTemplateRequests(status));
    }

    @PutMapping("/template-requests/{id}/status")
    public ResponseEntity<UserResumeTemplateRequest> reviewTemplateRequest(
            @PathVariable String id,
            @Valid @RequestBody TemplateStatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String adminId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        UserResumeTemplateRequest reviewed = adminService.reviewTemplateRequest(
                id, request.getStatus(), request.getAdminFeedback(), adminId);
        return ResponseEntity.ok(reviewed);
    }

    // --- Uploaded Resumes Review ---
    @GetMapping("/uploaded-resumes")
    public ResponseEntity<List<UploadedResume>> getAllUploadedResumes() {
        return ResponseEntity.ok(adminService.getAllUploadedResumes());
    }
}
