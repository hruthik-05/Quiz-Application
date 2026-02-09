package com.projectquiz.demo.models;

import java.util.Map;

public class UserResponse {
    private String userId;
    private String subject;
    Map<String, String> responses;
    private long startTime;
    private long endTime;
    private int timeLimit;
    
    public int getTimeLimit() {
        return timeLimit;
    }
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public Map<String, String> getResponses() {
        return responses;
    }
    public void setResponses(Map<String, String> responses) {
        this.responses = responses;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
