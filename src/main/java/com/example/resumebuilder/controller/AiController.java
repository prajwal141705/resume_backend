package com.example.resumebuilder.controller;

import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.model.dto.AiMatchRequest;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.service.AiMatchingService;
import com.example.resumebuilder.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
