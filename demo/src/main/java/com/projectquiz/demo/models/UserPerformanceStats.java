package com.projectquiz.demo.models;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "user_performance")
public class UserPerformanceStats {
    @Id
    private String id;
    private String userId;
    

    private Map<String, Double> subjectAccuracy;
    

    private Map<String, Double> difficultyAccuracy;


    private Map<String, Integer> subjectAttempts;



    private java.util.List<Map<String, Object>> quizHistory;
    

    private LocalDateTime lastUpdated;
}
