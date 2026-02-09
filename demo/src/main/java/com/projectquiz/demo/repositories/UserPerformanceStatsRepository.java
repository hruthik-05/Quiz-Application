package com.projectquiz.demo.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.projectquiz.demo.models.UserPerformanceStats;

public interface UserPerformanceStatsRepository extends MongoRepository<UserPerformanceStats, String> {
    Optional<UserPerformanceStats> findByUserId(String userId);
}
