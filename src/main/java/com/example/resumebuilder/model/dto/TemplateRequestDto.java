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
public class TemplateRequestDto {
    @NotBlank(message = "Template name is required")
    private String templateName;

    private String description;

    private String previewUrl;
}
