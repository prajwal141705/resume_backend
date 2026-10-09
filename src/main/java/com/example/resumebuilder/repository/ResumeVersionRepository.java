package com.example.resumebuilder.repository;

import com.example.resumebuilder.model.ResumeVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeVersionRepository extends MongoRepository<ResumeVersion, String> {
    List<ResumeVersion> findByResumeIdOrderByVersionNumberDesc(String resumeId);
    void deleteByResumeId(String resumeId);
    long countByResumeId(String resumeId);
}
