package com.projectquiz.demo.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.AdminAnalyticsDto;
import com.projectquiz.demo.models.Contest;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.models.ContestAttemptDto;
import com.projectquiz.demo.models.QuizAttempt;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.models.UserPerformanceStats;
import com.projectquiz.demo.models.UserPerformanceStatsDto;
import com.projectquiz.demo.repositories.ContestAttemptRepository;
import com.projectquiz.demo.repositories.ContestRepository;
import com.projectquiz.demo.repositories.QuestionRepository;
import com.projectquiz.demo.repositories.UserPerformanceStatsRepository;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class AnalyticsService {
    @Autowired
    UserPerformanceStatsRepository statsRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ContestRepository contestRepository;
    
    @Autowired
    QuestionRepository questionRepository;
    
    @Autowired
    ContestAttemptRepository attemptRepository;

    public AdminAnalyticsDto getAdminStats() {
        long users = userRepository.count();
        long activeContests = contestRepository.findByIsActiveTrue().size();
        long questions = questionRepository.count();
        long attempts = attemptRepository.count();
        
        return new AdminAnalyticsDto(users, activeContests, questions, attempts);
    }

    public void updateStats(String userId, String subject, ResultDto result) {
        UserPerformanceStats stats = statsRepository.findByUserId(userId)
            .orElse(new UserPerformanceStats());
        
        if (stats.getUserId() == null) {
            stats.setUserId(userId);
            stats.setSubjectAccuracy(new HashMap<>());
            stats.setSubjectAttempts(new HashMap<>());
            stats.setDifficultyAccuracy(new HashMap<>());
            stats.setQuizHistory(new ArrayList<>());
        }
        

        if (stats.getDifficultyAccuracy() == null) stats.setDifficultyAccuracy(new HashMap<>());
        if (stats.getQuizHistory() == null) stats.setQuizHistory(new ArrayList<>());


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

    public List<UserPerformanceStatsDto> getAllUserStats() {
        List<UserPerformanceStats> stats = statsRepository.findAll();
        List<String> userIds = stats.stream().map(UserPerformanceStats::getUserId).distinct().toList();
        List<User> users = (List<User>) userRepository.findAllById(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

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

    public List<ContestAttemptDto> getAllContestAttempts() {
        List<ContestAttempt> attempts = attemptRepository.findAll();
        
        List<String> userIds = attempts.stream().map(ContestAttempt::getUserId).distinct().toList();
        List<User> users = (List<User>) userRepository.findAllById(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        
        List<String> contestIds = attempts.stream().map(ContestAttempt::getContestId).distinct().toList();
        List<Contest> contests = (List<Contest>) contestRepository.findAllById(contestIds);
        Map<String, Contest> contestMap = contests.stream().collect(Collectors.toMap(Contest::getId, c -> c));
        
        return attempts.stream().map(a -> {
            ContestAttemptDto dto = new ContestAttemptDto();
            dto.setId(a.getId());
            dto.setContestId(a.getContestId());
            dto.setUserId(a.getUserId());
            dto.setScore(a.getScore());
            dto.setTimeTaken(a.getTimeTaken());
            dto.setSubmittedAt(a.getSubmittedAt());
            dto.setResponses(a.getResponses());
            
            Contest c = contestMap.get(a.getContestId());
            if (c != null) {
                dto.setContestTitle(c.getTitle());
            } else {
                dto.setContestTitle("Unknown Contest");
            }
            
            User u = userMap.get(a.getUserId());
            if (u != null) {
                dto.setUsername(u.getUsername());
                dto.setEmail(u.getEmail());
            } else {
                dto.setUsername("Unknown User");
            }
            return dto;
        }).toList();
    }
}
