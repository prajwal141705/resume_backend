package com.example.resumebuilder.repository;

import com.example.resumebuilder.model.ResumeTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeTemplateRepository extends MongoRepository<ResumeTemplate, String> {
    Optional<ResumeTemplate> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<ResumeTemplate> findByEnabledTrue();
    List<ResumeTemplate> findByEnabledTrueOrderByCreatedAtAsc();
    List<ResumeTemplate> findAllByOrderByCreatedAtAsc();
    long countByEnabledTrue();
}
