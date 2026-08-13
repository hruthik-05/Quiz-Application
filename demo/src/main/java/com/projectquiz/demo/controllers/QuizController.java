package com.projectquiz.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.models.UserResponse;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.repositories.UserRepository;
import com.projectquiz.demo.services.AdaptiveQuizService;
import com.projectquiz.demo.services.EmailService;
import com.projectquiz.demo.services.EvaluationService;
import com.projectquiz.demo.services.QuizService;
import com.projectquiz.demo.services.UserService;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*", maxAge = 3600)
public class QuizController {
    @Autowired
    QuizService qService;
    @Autowired
    EvaluationService evaluationService;
    @Autowired
    AdaptiveQuizService adaptiveService;
    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    private String getAuthenticatedUserId() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.projectquiz.demo.security.services.UserDetailsImpl) {
                return ((com.projectquiz.demo.security.services.UserDetailsImpl) principal).getId();
            }
        }
        return null;
    }

    @RequestMapping("/create/{numberOfQuestions}")
    public List<QuestionDto> createQuiz(@PathVariable int numberOfQuestions){
        if (numberOfQuestions <= 0 || numberOfQuestions > 100) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Number of questions must be between 1 and 100");
        }
        return qService.createQuiz(numberOfQuestions);
    }
    @RequestMapping("/createsubject/{subject}/{numberOfQuestions}")
    public List<QuestionDto> createSubjectQuiz(@PathVariable String subject, @PathVariable int numberOfQuestions){
        if (numberOfQuestions <= 0 || numberOfQuestions > 100) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Number of questions must be between 1 and 100");
        }
        return qService.createCustomQuiz(subject, "MIXED", numberOfQuestions);
    }
    
    @RequestMapping("/custom/{subject}/{difficulty}/{numberOfQuestions}")
    public List<QuestionDto> createCustomQuiz(@PathVariable String subject, @PathVariable String difficulty, @PathVariable int numberOfQuestions){
        if (numberOfQuestions <= 0 || numberOfQuestions > 100) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Number of questions must be between 1 and 100");
        }
        return qService.createCustomQuiz(subject, difficulty, numberOfQuestions);
    }
    @RequestMapping("/adaptive/{userId}/{subject}/{numberOfQuestions}")
    public List<QuestionDto> createAdaptiveQuiz(@PathVariable String userId, @PathVariable String subject, @PathVariable int numberOfQuestions) {
        if (numberOfQuestions <= 0 || numberOfQuestions > 100) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Number of questions must be between 1 and 100");
        }
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        com.projectquiz.demo.security.services.UserDetailsImpl userDetails = (com.projectquiz.demo.security.services.UserDetailsImpl) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userDetails.getId().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access denied");
        }
        return adaptiveService.generateAdaptiveQuiz(userId, subject, numberOfQuestions);
    }

    @RequestMapping("/submit")
    public ResultDto submitQuiz(@RequestBody UserResponse userResponse) {
        String authUserId = getAuthenticatedUserId();
        userResponse.setUserId(authUserId);
        
        long now = System.currentTimeMillis();
        if (userResponse.getStartTime() <= 0 || userResponse.getStartTime() > now) {
            userResponse.setStartTime(now);
        }
        userResponse.setEndTime(now);

        ResultDto score = evaluationService.pointsBasedEvaluation(userResponse);
        

        try {
            if (userResponse.getUserId() != null) {
                User user = userService.getUserById(userResponse.getUserId());
                if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                    String subject = "Quiz Result: " + (userResponse.getSubject() != null ? userResponse.getSubject() : "Practice Quiz");
                    String body = String.format("Hi %s,\n\nYou scored %d points in your quiz.\n\nKeep learning!", 
                        user.getUsername(), score.getTotalScore());
                    emailService.sendQuizResult(user.getEmail(), subject, body);
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }

        return score;
    }
}
