package com.projectquiz.demo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.QuestionDto;
@Service 
public class QuizService {
    @Autowired
    QuestionService qService;
    public List<QuestionDto> createQuiz(int numberOfQuestions){
        return createCustomQuiz("ALL", "MIXED", numberOfQuestions);
    }
    public List<QuestionDto> createSubjectQuiz(String subject, int numberOfQuestions) {
        return createCustomQuiz(subject, "MIXED", numberOfQuestions);
    }

    public List<QuestionDto> createCustomQuiz(String subject, String difficulty, int numberOfQuestions) {
        Difficulty diffEnum = null;
        if (difficulty != null && !difficulty.equalsIgnoreCase("MIXED")) {
            try {
                diffEnum = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore, fallback to null (mixed difficulty)
            }
        }
        
        List<Question> pool = qService.getRandomQuestions(subject, diffEnum, numberOfQuestions);
        
        List<QuestionDto> quizQuestionsDto = new ArrayList<>();
        for (Question q : pool) {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setPoints(q.getPoints());
            dto.setQuestion(q.getQuestionText());
            dto.setOptions(q.getOptions());
            quizQuestionsDto.add(dto);
        }
        Collections.shuffle(quizQuestionsDto);
        return quizQuestionsDto;
    }
    }

