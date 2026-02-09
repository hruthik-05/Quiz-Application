package com.projectquiz.demo.models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AdminAnalyticsDto {
    private long totalUsers;
    private long activeContests;
    private long totalQuestions;
    private long totalAttempts;
}
