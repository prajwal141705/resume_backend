package com.example.resumebuilder.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateStatusUpdateRequest {
    @NotBlank(message = "Status is required")
    private String status; // PENDING, APPROVED, REJECTED

    private String adminFeedback;
}
