package com.projectquiz.demo.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Contest;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.repositories.ContestAttemptRepository;
import com.projectquiz.demo.repositories.ContestRepository;
import com.projectquiz.demo.repositories.QuestionRepository;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class ContestService {

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    ContestAttemptRepository attemptRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    public Contest createContest(Contest contest) {

        return contestRepository.save(contest);
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }
    
    public List<Contest> getActiveContests() {
        return contestRepository.findByIsActiveTrue();
    }

    public Contest getContestById(String id) {
        return contestRepository.findById(id).orElse(null);
    }

    public List<Question> getQuestionsForContest(String contestId) {
        Contest contest = getContestById(contestId);
        if (contest == null || contest.getQuestionIds() == null) {
            return List.of();
        }
        Iterable<Question> questions = questionRepository.findAllById(contest.getQuestionIds());
        return (List<Question>) questions;
    }

    public ContestAttempt submitContest(String contestId, String userId, Map<String, String> responses, long timeTaken) {

        Contest contest = getContestById(contestId);
        if (contest == null) {
            throw new RuntimeException("Contest not found");
        }


        int attempts = attemptRepository.countByUserIdAndContestId(userId, contestId);
        if (attempts >= contest.getMaxAttempts()) {
             throw new RuntimeException("Maximum attempts reached for this contest!");
        }
        

        long now = System.currentTimeMillis();


        if (now < contest.getStartTime()) {
             throw new RuntimeException("Contest has not started yet.");
        }
        if (now > contest.getEndTime() + (5 * 60 * 1000)) { 
             throw new RuntimeException("Contest submission window closed.");
        }


        double score = 0;
        int correct = 0;
        int wrong = 0;

        for (Map.Entry<String, String> entry : responses.entrySet()) {
            String questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Optional<Question> qOpt = questionRepository.findById(questionId);
            if (qOpt.isPresent()) {
                Question q = qOpt.get();
                if (q.getAnswer().trim().equalsIgnoreCase(userAnswer.trim())) {
                    score += 1.0; 
                    correct++;
                } else {
                    wrong++;
                    if (contest.isAllowNegativeMarking()) {
                        score -= contest.getNegativeMarkFactor();
                    }
                }
            }
        }
        




        ContestAttempt attempt = new ContestAttempt();
        attempt.setContestId(contestId);
        attempt.setUserId(userId);
        attempt.setResponses(responses);
        attempt.setScore(score);
        attempt.setTimeTaken(timeTaken);
        attempt.setSubmittedAt(now);

        ContestAttempt savedAttempt = attemptRepository.save(attempt);


        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                String emailBody = String.format(
                    "Hi %s,\n\nYou have completed the contest: %s.\n\nScore: %.2f\nCorrect: %d\nWrong: %d\n\nKeep improving!",
                    user.getUsername(), contest.getTitle(), score, correct, wrong
                );
                emailService.sendQuizResult(user.getEmail(), "Contest Result: " + contest.getTitle(), emailBody);
            }
        }

        return savedAttempt;
    }
    public List<com.projectquiz.demo.models.ContestAttemptDto> getAttemptsForContest(String contestId) {
        List<ContestAttempt> attempts = attemptRepository.findByContestId(contestId);
        List<String> userIds = attempts.stream().map(ContestAttempt::getUserId).distinct().toList();
        List<User> users = (List<User>) userRepository.findAllById(userIds);
        Map<String, User> userMap = users.stream().collect(java.util.stream.Collectors.toMap(User::getId, u -> u));

        return attempts.stream().map(a -> {
            com.projectquiz.demo.models.ContestAttemptDto dto = new com.projectquiz.demo.models.ContestAttemptDto();
            dto.setId(a.getId());
            dto.setContestId(a.getContestId());
            dto.setUserId(a.getUserId());
            dto.setScore(a.getScore());
            dto.setTimeTaken(a.getTimeTaken());
            dto.setSubmittedAt(a.getSubmittedAt());
            dto.setResponses(a.getResponses());

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
    
    public List<ContestAttempt> getStudentAttempts(String userId) {
        return attemptRepository.findByUserId(userId);
    }
}
