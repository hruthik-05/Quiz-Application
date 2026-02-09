package com.projectquiz.demo.models;

import lombok.Data;
import java.util.Map;
import java.time.LocalDateTime;

@Data
public class UserPerformanceStatsDto {
    private String id;
    private String userId;
    private String username; 
    
    private Map<String, Double> subjectAccuracy;
    private Map<String, Integer> subjectAttempts;
    private LocalDateTime lastUpdated;
}
