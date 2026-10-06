package com.example.resumebuilder.model.dto;

import com.example.resumebuilder.model.UploadedResume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long totalResumes;
    private long totalUploadedResumes;
    private long totalTemplates;
    private long totalJobs;
    private long pendingTemplateRequests;
    private List<UserDto> recentUsers;
    private List<UploadedResume> recentUploads;
}
