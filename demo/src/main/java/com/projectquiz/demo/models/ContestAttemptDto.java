package com.projectquiz.demo.models;

import lombok.Data;
import java.util.Map;

@Data
public class ContestAttemptDto {
    private String id;
    private String contestId;
    private String userId; 
    private String username; 
    private String email; 
    private double score;
    private long timeTaken;
    private long submittedAt;
    


    private Map<String, String> responses; 
}
