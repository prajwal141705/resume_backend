package com.example.resumebuilder.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtsAnalysisResponse {
    private int overallScore; // 0-100
    private String grade; // e.g., "Excellent", "Good", "Needs Improvement"
    private Map<String, Integer> categoryScores; // Contact Info, Structure, Skills, Experience, Keywords, Education, Formatting, Readability
    private List<String> strongAreas;
    private List<String> areasToImprove;
    private List<String> missingInformation;
    private List<String> actionableSuggestions;
    private String disclaimer;
}
