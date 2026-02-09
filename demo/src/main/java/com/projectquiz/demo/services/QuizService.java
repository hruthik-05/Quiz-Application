package com.projectquiz.demo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.QuestionDto;
@Service 
public class QuizService {
    @Autowired
    QuestionService qService;
    public List<QuestionDto> createQuiz(int numberOfQuestions){
        List<Question> quizQuestions = qService.getAllQuestions();
        
        List<QuestionDto> quizQuestionsDto = new ArrayList<>();
        for (Question q : quizQuestions) {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setPoints(q.getPoints());
            dto.setQuestion(q.getQuestionText());
            dto.setOptions(q.getOptions());
            quizQuestionsDto.add(dto);
        }

        Collections.shuffle(quizQuestionsDto);
        return quizQuestionsDto.subList(0, Math.min(numberOfQuestions, quizQuestionsDto.size()) );
        }
    public List<QuestionDto> createSubjectQuiz(String subject, int numberOfQuestions) {
        return createCustomQuiz(subject, "MIXED", numberOfQuestions);
    }

    public List<QuestionDto> createCustomQuiz(String subject, String difficulty, int numberOfQuestions) {
        List<Question> pool = new ArrayList<>();
        
        if (difficulty == null || difficulty.equalsIgnoreCase("MIXED")) {

             if (subject.equalsIgnoreCase("ALL")) {
                 pool = qService.getAllQuestions();
             } else {
                 pool = qService.getQuestionsByCategory(subject);
             }
        } else {

            try {
                com.projectquiz.demo.models.Difficulty diffEnum = com.projectquiz.demo.models.Difficulty.valueOf(difficulty.toUpperCase());
                if (subject.equalsIgnoreCase("ALL")) {
                    pool = qService.getQuestionsByDifficulty(diffEnum);
                } else {
                     pool = qService.getQuestionsByCategoryAndDifficulty(subject, diffEnum);
                }
            } catch (IllegalArgumentException e) {

                 pool = qService.getQuestionsByCategory(subject);
            }
        }

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
        return quizQuestionsDto.subList(0, Math.min(numberOfQuestions, quizQuestionsDto.size()) );
    }
    }

