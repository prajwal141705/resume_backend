package com.example.resumebuilder.service.impl;

import com.example.resumebuilder.exception.AiServiceException;
import com.example.resumebuilder.model.dto.AiMatchResponse;
import com.example.resumebuilder.service.LlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("geminiLlmService")
public class GeminiLlmService implements LlmService {

    @Value("${ai.gemini.api-key:}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String model;

    @Value("${ai.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiLlmService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public AiMatchResponse analyzeResumeAgainstJob(String resumeText, String jobDescription) {
        if (apiKey == null || apiKey.trim().isEmpty() || "your_gemini_api_key_here".equals(apiKey)) {
            log.warn("Gemini API key is not configured. Falling back to local heuristic analysis.");
            return runHeuristicFallback(resumeText, jobDescription);
        }

        String prompt = buildPrompt(resumeText, jobDescription);
        String url = String.format("%s/models/%s:generateContent?key=%s", baseUrl, model, apiKey);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.2
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Sending request to Gemini API (model: {})", model);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Gemini API returned non-2xx status: {}", response.getStatusCode());
                throw new AiServiceException("Gemini API request failed with status: " + response.getStatusCode());
            }

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            JsonNode candidatesNode = rootNode.path("candidates");
            if (!candidatesNode.isArray() || candidatesNode.isEmpty()) {
                log.error("Gemini API returned empty candidates");
                throw new AiServiceException("No candidates returned from Gemini AI");
            }

            String textResponse = candidatesNode.get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return parseAiJson(textResponse);

        } catch (RestClientException ex) {
            log.error("Network or HTTP error communicating with Gemini API: {}", ex.getMessage());
            throw new AiServiceException("Failed to communicate with AI service: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Error processing Gemini API response: {}", ex.getMessage());
            throw new AiServiceException("Error processing AI response", ex);
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return "You are an expert technical recruiter and talent evaluator.\n"
                + "Analyze the following resume against the job description strictly and realistically.\n\n"
                + "RESUME:\n" + resumeText + "\n\n"
                + "JOB DESCRIPTION:\n" + jobDescription + "\n\n"
                + "Rules:\n"
                + "1. matchScore must be an integer between 0 and 100 representing overall fit.\n"
                + "2. matchedSkills must contain only technical and relevant skills explicitly present in both the resume and the job description.\n"
                + "3. missingSkills must contain important job requirements/skills missing from the resume.\n"
                + "4. aiRecommendations must contain actionable, concise recommendations for the candidate to improve their fit.\n"
                + "5. Do NOT invent skills or experience that are not in the resume.\n"
                + "6. Return ONLY valid JSON in this exact structure without markdown fences or any extra text:\n"
                + "{\n"
                + "  \"matchScore\": 85,\n"
                + "  \"matchedSkills\": [\"Skill1\", \"Skill2\"],\n"
                + "  \"missingSkills\": [\"Skill3\"],\n"
                + "  \"aiRecommendations\": [\"Recommendation 1\", \"Recommendation 2\"]\n"
                + "}";
    }

    private AiMatchResponse parseAiJson(String textResponse) {
        try {
            String cleanedJson = cleanJsonString(textResponse);
            AiMatchResponse response = objectMapper.readValue(cleanedJson, AiMatchResponse.class);

            // Sanitize score to 0-100 range
            int score = Math.max(0, Math.min(100, response.getMatchScore()));
            response.setMatchScore(score);

            if (response.getMatchedSkills() == null) response.setMatchedSkills(List.of());
            if (response.getMissingSkills() == null) response.setMissingSkills(List.of());
            if (response.getAiRecommendations() == null) response.setAiRecommendations(List.of());

            return response;
        } catch (Exception e) {
            log.error("Failed to parse JSON from AI response: {}. Error: {}", textResponse, e.getMessage());
            throw new AiServiceException("Failed to parse response from AI service", e);
        }
    }

    private String cleanJsonString(String raw) {
        if (raw == null) return "{}";
        String trimmed = raw.trim();
        // Remove markdown ```json ... ``` code blocks if present
        if (trimmed.startsWith("```")) {
            Pattern pattern = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)\\s*```");
            Matcher matcher = pattern.matcher(trimmed);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }
        return trimmed;
    }

    private AiMatchResponse runHeuristicFallback(String resumeText, String jobDescription) {
        log.info("Running local heuristic resume matching algorithm");
        String resumeLower = resumeText.toLowerCase();
        String jobLower = jobDescription.toLowerCase();

        List<String> commonTech = List.of(
                "Java", "Spring Boot", "React", "Node.js", "Python", "Docker", "Kubernetes",
                "AWS", "MongoDB", "SQL", "PostgreSQL", "JavaScript", "TypeScript", "HTML", "CSS",
                "Git", "REST API", "Microservices", "GraphQL", "Redis", "Kafka", "CI/CD"
        );

        List<String> matched = commonTech.stream()
                .filter(skill -> resumeLower.contains(skill.toLowerCase()) && jobLower.contains(skill.toLowerCase()))
                .toList();

        List<String> missing = commonTech.stream()
                .filter(skill -> !resumeLower.contains(skill.toLowerCase()) && jobLower.contains(skill.toLowerCase()))
                .toList();

        int score = matched.isEmpty() && missing.isEmpty() ? 70 :
                (int) Math.round(((double) matched.size() / Math.max(1, (matched.size() + missing.size()))) * 100);
        score = Math.max(25, Math.min(95, score));

        List<String> recommendations = List.of(
                "Highlight quantifiable metrics and results in your work experience bullet points.",
                "Align your project descriptions closely with the key responsibilities in the job posting.",
                missing.isEmpty() ? "Ensure your resume layout is ATS-friendly and cleanly structured."
                        : "Consider adding experience or project work demonstrating knowledge of: " + String.join(", ", missing)
        );

        return AiMatchResponse.builder()
                .matchScore(score)
                .matchedSkills(matched)
                .missingSkills(missing)
                .aiRecommendations(recommendations)
                .build();
    }
}
