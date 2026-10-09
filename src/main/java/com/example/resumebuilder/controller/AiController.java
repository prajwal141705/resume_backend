package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.Resume;
import com.example.resumebuilder.model.dto.AiAssistDtos;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.model.dto.AtsAnalysisResponse;
import com.example.resumebuilder.service.AiMatchingService;
import com.example.resumebuilder.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiMatchingService aiMatchingService;
    private final AuthService authService;

    @PostMapping("/match")
    public ResponseEntity<AiMatchResponse> matchResume(
            @Valid @RequestBody AiMatchRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("AI match requested for resume: {} by user: {}", request.getResumeId(), userId);
        AiMatchResponse response = aiMatchingService.matchResumeWithJob(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ats-score/{resumeId}")
    public ResponseEntity<AtsAnalysisResponse> getAtsScore(
            @PathVariable String resumeId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        String userId = currentUser != null ? currentUser.getId() : authService.getCurrentUserId();
        log.info("ATS score requested for resume: {} by user: {}", resumeId, userId);
        AtsAnalysisResponse response = aiMatchingService.analyzeAts(resumeId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ats-analyze-direct")
    public ResponseEntity<AtsAnalysisResponse> analyzeAtsDirect(@RequestBody Resume resume) {
        return ResponseEntity.ok(aiMatchingService.analyzeAtsDirect(resume));
    }

    @PostMapping("/generate-summary")
    public ResponseEntity<AiAssistDtos.SummaryGenerateResponse> generateSummary(
            @RequestBody AiAssistDtos.SummaryGenerateRequest request) {
        return ResponseEntity.ok(aiMatchingService.generateSummary(request));
    }

    @PostMapping("/improve-section")
    public ResponseEntity<AiAssistDtos.SectionImproveResponse> improveSection(
            @RequestBody AiAssistDtos.SectionImproveRequest request) {
        return ResponseEntity.ok(aiMatchingService.improveSection(request));
    }

    @PostMapping("/suggest-skills")
    public ResponseEntity<AiAssistDtos.SkillSuggestionResponse> suggestSkills(
            @RequestBody AiAssistDtos.SkillSuggestionRequest request) {
        return ResponseEntity.ok(aiMatchingService.suggestSkills(request));
    }

    @PostMapping("/analyze-job-description")
    public ResponseEntity<AiAssistDtos.JobAnalysisResponse> analyzeJobDescription(
            @RequestBody AiAssistDtos.JobAnalysisRequest request) {
        return ResponseEntity.ok(aiMatchingService.analyzeJobDescription(request.getJobDescription()));
    }
}
