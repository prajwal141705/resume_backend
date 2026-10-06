package com.example.resumebuilder.repository;

import com.example.resumebuilder.model.UploadedResume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedResumeRepository extends MongoRepository<UploadedResume, String> {
    List<UploadedResume> findByUserIdOrderByCreatedAtDesc(String userId);
    List<UploadedResume> findAllByOrderByCreatedAtDesc();
    long countByUserId(String userId);
}
