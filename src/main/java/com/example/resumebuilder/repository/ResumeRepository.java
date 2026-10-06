package com.example.resumebuilder.repository;

import com.example.resumebuilder.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {
    List<Resume> findByUserId(String userId);
    Optional<Resume> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);
}
