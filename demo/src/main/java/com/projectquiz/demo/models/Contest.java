package com.projectquiz.demo.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.Setter;

@Data
@Document(collection = "contests")
@Getter
@Setter
public class Contest {
    @Id
    private String id;
    private String title;
    private String description;
    private long startTime; 
    private long endTime; 
    private int durationMinutes;
    private List<String> questionIds; 
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Question> contestQuestions;

    private boolean isActive;
    private boolean allowNegativeMarking;
    private double negativeMarkFactor; 
    
    private int maxAttempts = 1; 
    private long resultReleaseTime; 
}
