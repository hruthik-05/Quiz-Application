package com.projectquiz.demo.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.QuizAttempt;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.models.UserPerformanceStats;
import com.projectquiz.demo.models.UserPerformanceStatsDto;
import com.projectquiz.demo.repositories.UserPerformanceStatsRepository;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class AnalyticsService {
    @Autowired
    UserPerformanceStatsRepository statsRepository;

    @Autowired
    com.projectquiz.demo.repositories.UserRepository userRepository;
    @Autowired
    com.projectquiz.demo.repositories.ContestRepository contestRepository;
    @Autowired
    com.projectquiz.demo.repositories.QuestionRepository questionRepository;
    @Autowired
    com.projectquiz.demo.repositories.ContestAttemptRepository attemptRepository;

    public com.projectquiz.demo.models.AdminAnalyticsDto getAdminStats() {
        long users = userRepository.count();
        long activeContests = contestRepository.findByIsActiveTrue().size();
        long questions = questionRepository.count();
        long attempts = attemptRepository.count();
        
        return new com.projectquiz.demo.models.AdminAnalyticsDto(users, activeContests, questions, attempts);
    }

    public void updateStats(String userId, String subject, ResultDto result) {
        UserPerformanceStats stats = statsRepository.findByUserId(userId)
            .orElse(new UserPerformanceStats());
        
        if (stats.getUserId() == null) {
            stats.setUserId(userId);
            stats.setSubjectAccuracy(new HashMap<>());
            stats.setSubjectAttempts(new HashMap<>());
            stats.setDifficultyAccuracy(new HashMap<>());
            stats.setQuizHistory(new java.util.ArrayList<>());
        }
        

        if (stats.getDifficultyAccuracy() == null) stats.setDifficultyAccuracy(new HashMap<>());
        if (stats.getQuizHistory() == null) stats.setQuizHistory(new java.util.ArrayList<>());


        double currentQuizAccuracy = 0;
        int totalQuestions = result.getCorrect() + result.getWrong();
        if (totalQuestions > 0) {
            currentQuizAccuracy = (double) result.getCorrect() / totalQuestions * 100.0;
        }


        int currentAttempts = stats.getSubjectAttempts().getOrDefault(subject, 0);
        double oldSubjectAvg = stats.getSubjectAccuracy().getOrDefault(subject, 0.0);


        double newSubjectAvg = ((oldSubjectAvg * currentAttempts) + currentQuizAccuracy) / (currentAttempts + 1);
        
        stats.getSubjectAttempts().put(subject, currentAttempts + 1);
        stats.getSubjectAccuracy().put(subject, newSubjectAvg);









        

        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("date", LocalDateTime.now().toString());
        historyEntry.put("score", currentQuizAccuracy);
        historyEntry.put("subject", subject);
        
        stats.getQuizHistory().add(historyEntry);

        if (stats.getQuizHistory().size() > 10) {
            stats.getQuizHistory().remove(0);
        }

        stats.setLastUpdated(LocalDateTime.now());
        statsRepository.save(stats);
    }
    
    public UserPerformanceStats getUserStats(String userId) {
        return statsRepository.findByUserId(userId).orElse(null);
    }

    public java.util.List<UserPerformanceStatsDto> getAllUserStats() {
        java.util.List<UserPerformanceStats> stats = statsRepository.findAll();
        java.util.List<String> userIds = stats.stream().map(UserPerformanceStats::getUserId).distinct().toList();
        java.util.List<User> users = (java.util.List<User>) userRepository.findAllById(userIds);
        java.util.Map<String, User> userMap = users.stream().collect(java.util.stream.Collectors.toMap(User::getId, u -> u));

        return stats.stream().map(s -> {
            UserPerformanceStatsDto dto = new UserPerformanceStatsDto();
            dto.setId(s.getId());
            dto.setUserId(s.getUserId());
            dto.setSubjectAccuracy(s.getSubjectAccuracy());
            dto.setSubjectAttempts(s.getSubjectAttempts());
            dto.setLastUpdated(s.getLastUpdated());
            
            User u = userMap.get(s.getUserId());
            if (u != null) {
                dto.setUsername(u.getUsername());
            } else {
                dto.setUsername("Unknown");
            }
            return dto;
        }).toList();
    }

    public java.util.List<com.projectquiz.demo.models.ContestAttempt> getAllContestAttempts() {
        return attemptRepository.findAll();
    }
}
