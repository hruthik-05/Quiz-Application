package com.projectquiz.demo.models;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "contests")
public class Contest {
    @Id
    private String id;
    private String title;
    private String description;
    private long startTime; 
    private long endTime; 
    private int durationMinutes;
    private List<String> questionIds; 
    private boolean isActive;
    private boolean allowNegativeMarking;
    private double negativeMarkFactor; 
    
    private int maxAttempts = 1; 
    private long resultReleaseTime; 
}
