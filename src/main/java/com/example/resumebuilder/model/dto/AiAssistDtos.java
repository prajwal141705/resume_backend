package com.example.resumebuilder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public final class AiAssistDtos {

    private AiAssistDtos() {}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryGenerateRequest {
        private String targetRole;
        private String yearsOfExperience;
        private List<String> keySkills;
        private String tone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryGenerateResponse {
        private String summary;
        private List<String> alternateSummaries;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionImproveRequest {
        private String sectionName;
        private String currentText;
        private String targetRole;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionImproveResponse {
        private String improvedText;
        private List<String> bulletSuggestions;
        private List<String> powerVerbsUsed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillSuggestionRequest {
        private String targetRole;
        private List<String> existingSkills;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillSuggestionResponse {
        private List<String> recommendedTechnicalSkills;
        private List<String> recommendedSoftSkills;
        private List<String> trendingTools;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobAnalysisRequest {
        private String jobDescription;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobAnalysisResponse {
        private String jobTitle;
        private List<String> requiredSkills;
        private List<String> preferredSkills;
        private List<String> keyResponsibilities;
        private List<String> importantKeywords;
        private String experienceLevel;
    }
}
