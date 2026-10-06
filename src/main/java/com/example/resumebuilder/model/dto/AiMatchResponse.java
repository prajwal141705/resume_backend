package com.example.resumebuilder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMatchResponse {
    private int matchScore;

    @Builder.Default
    private List<String> matchedSkills = new ArrayList<>();

    @Builder.Default
    private List<String> missingSkills = new ArrayList<>();

    @Builder.Default
    private List<String> aiRecommendations = new ArrayList<>();
}
