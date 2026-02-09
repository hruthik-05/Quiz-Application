package com.projectquiz.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.models.UserResponse;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.services.AdaptiveQuizService;
import com.projectquiz.demo.services.EvaluationService;
import com.projectquiz.demo.services.QuizService;

@RestController
@RequestMapping("/api/quiz")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*", maxAge = 3600)
public class QuizController {
    @Autowired
    QuizService qService;
    @Autowired
    EvaluationService evaluationService;
    @Autowired
    AdaptiveQuizService adaptiveService;
    @Autowired
    com.projectquiz.demo.services.EmailService emailService;
    @Autowired
    com.projectquiz.demo.repositories.UserRepository userRepository;

    @RequestMapping("/create/{numberOfQuestions}")
    public List<QuestionDto> createQuiz(@PathVariable int numberOfQuestions){
        return qService.createQuiz(numberOfQuestions);
    }
    @RequestMapping("/createsubject/{subject}/{numberOfQuestions}")
    public List<QuestionDto> createSubjectQuiz(@PathVariable String subject, @PathVariable int numberOfQuestions){
        return qService.createCustomQuiz(subject, "MIXED", numberOfQuestions);
    }
    
    @RequestMapping("/custom/{subject}/{difficulty}/{numberOfQuestions}")
    public List<QuestionDto> createCustomQuiz(@PathVariable String subject, @PathVariable String difficulty, @PathVariable int numberOfQuestions){
        return qService.createCustomQuiz(subject, difficulty, numberOfQuestions);
    }
    @RequestMapping("/adaptive/{userId}/{subject}/{numberOfQuestions}")
    public List<QuestionDto> createAdaptiveQuiz(@PathVariable String userId, @PathVariable String subject, @PathVariable int numberOfQuestions) {
        return adaptiveService.generateAdaptiveQuiz(userId, subject, numberOfQuestions);
    }

    @RequestMapping("/submit")
    public ResultDto submitQuiz(@RequestBody UserResponse userResponse) {
        ResultDto score = evaluationService.pointsBasedEvaluation(userResponse);
        

        try {
            if (userResponse.getUserId() != null) {
                com.projectquiz.demo.models.User user = userRepository.findById(userResponse.getUserId()).orElse(null);
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
