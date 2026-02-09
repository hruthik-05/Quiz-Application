package com.projectquiz.demo.models;

import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "contest_attempts")
public class ContestAttempt {
    @Id
    private String id;
    private String contestId;
    private String userId;
    private Map<String, String> responses; 
    private double score; 
    private long timeTaken; 
    private long submittedAt;
}
