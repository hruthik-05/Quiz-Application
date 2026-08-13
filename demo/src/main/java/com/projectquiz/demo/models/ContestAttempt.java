package com.projectquiz.demo.models;

import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "contest_attempts")
@org.springframework.data.mongodb.core.index.CompoundIndexes({
    @org.springframework.data.mongodb.core.index.CompoundIndex(name = "user_contest_attempt_idx", def = "{'userId': 1, 'contestId': 1, 'attemptNumber': 1}", unique = true)
})
public class ContestAttempt {
    @Id
    private String id;
    private String contestId;
    private String userId;
    private Map<String, String> responses; 
    private double score; 
    private long timeTaken; 
    private long submittedAt;

    private int attemptNumber;

    @org.springframework.data.annotation.Transient
    private Map<String, String> correctAnswers;
}
