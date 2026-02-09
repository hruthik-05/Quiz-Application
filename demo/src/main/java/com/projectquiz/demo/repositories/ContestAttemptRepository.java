package com.projectquiz.demo.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.projectquiz.demo.models.ContestAttempt;

public interface ContestAttemptRepository extends MongoRepository<ContestAttempt, String> {
    List<ContestAttempt> findByContestId(String contestId);
    List<ContestAttempt> findByUserId(String userId);
    Optional<ContestAttempt> findByUserIdAndContestId(String userId, String contestId);
    int countByUserIdAndContestId(String userId, String contestId);
}
