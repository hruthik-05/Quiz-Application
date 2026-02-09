package com.projectquiz.demo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.models.UserPerformanceStats;
import com.projectquiz.demo.repositories.QuestionRepository;
import com.projectquiz.demo.repositories.UserPerformanceStatsRepository;

@Service
public class AdaptiveQuizService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserPerformanceStatsRepository statsRepository;

    public List<QuestionDto> generateAdaptiveQuiz(String userId, String subject, int numberOfQuestions) {

        Optional<UserPerformanceStats> statsOpt = statsRepository.findByUserId(userId);
        
        double currentAccuracy = 0.0;
        if (statsOpt.isPresent() && statsOpt.get().getSubjectAccuracy() != null) {
            currentAccuracy = statsOpt.get().getSubjectAccuracy().getOrDefault(subject, 0.0); 
        } else {

            currentAccuracy = 50.0; 
        }


        int easyCount = 0, mediumCount = 0, hardCount = 0;

        if (currentAccuracy >= 70.0) {

            easyCount = (int) (numberOfQuestions * 0.2);
            mediumCount = (int) (numberOfQuestions * 0.3);
            hardCount = numberOfQuestions - easyCount - mediumCount;
        } else if (currentAccuracy >= 40.0) {

            easyCount = (int) (numberOfQuestions * 0.3);
            mediumCount = (int) (numberOfQuestions * 0.5);
            hardCount = numberOfQuestions - easyCount - mediumCount;
        } else {

            easyCount = (int) (numberOfQuestions * 0.5);
            mediumCount = (int) (numberOfQuestions * 0.3);
            hardCount = numberOfQuestions - easyCount - mediumCount;
        }


        List<Question> selectedQuestions = new ArrayList<>();
        selectedQuestions.addAll(fetchRandomQuestions(subject, Difficulty.EASY, easyCount));
        selectedQuestions.addAll(fetchRandomQuestions(subject, Difficulty.MEDIUM, mediumCount));
        selectedQuestions.addAll(fetchRandomQuestions(subject, Difficulty.HARD, hardCount));


        List<QuestionDto> quizDtos = new ArrayList<>();
        for (Question q : selectedQuestions) {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setPoints(q.getPoints());
            dto.setQuestion(q.getQuestionText());
            dto.setOptions(q.getOptions());
            quizDtos.add(dto);
        }

        Collections.shuffle(quizDtos);
        return quizDtos;
    }

    private List<Question> fetchRandomQuestions(String subject, Difficulty difficulty, int count) {
         if (count <= 0) return new ArrayList<>();
         
         List<Question> pool;
         if (subject == null || subject.equalsIgnoreCase("ALL")) {
             pool = questionRepository.findByDifficulty(difficulty);
         } else {
             pool = questionRepository.findByCategoryIgnoreCaseAndDifficulty(subject, difficulty);
         }
         
         if (pool.isEmpty()) return new ArrayList<>();

         Collections.shuffle(pool);
         return pool.subList(0, Math.min(count, pool.size()));
    }
}
