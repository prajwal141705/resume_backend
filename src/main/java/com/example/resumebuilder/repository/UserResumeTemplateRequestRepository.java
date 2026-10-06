package com.example.resumebuilder.repository;

import com.example.resumebuilder.model.UserResumeTemplateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserResumeTemplateRequestRepository extends MongoRepository<UserResumeTemplateRequest, String> {
    List<UserResumeTemplateRequest> findByUserIdOrderByCreatedAtDesc(String userId);
    List<UserResumeTemplateRequest> findAllByOrderByCreatedAtDesc();
    List<UserResumeTemplateRequest> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}
