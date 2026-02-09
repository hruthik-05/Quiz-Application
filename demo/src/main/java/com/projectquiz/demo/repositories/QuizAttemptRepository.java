package com.projectquiz.demo.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.projectquiz.demo.models.QuizAttempt;

public interface QuizAttemptRepository extends MongoRepository<QuizAttempt, String> {
    List<QuizAttempt> findByUserId(String userId);
}
