package com.example.resumebuilder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "resume_versions")
public class ResumeVersion {

    @Id
    private String id;

    @Indexed
    private String resumeId;

    @Indexed
    private String userId;

    private int versionNumber;

    private String versionName;

    private Resume resumeSnapshot;

    private String changeNotes;

    @CreatedDate
    private Instant createdAt;
}
