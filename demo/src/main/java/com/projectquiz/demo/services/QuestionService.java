package com.projectquiz.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.repositories.QuestionRepository;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionrepository;
    public List<Question> getAllQuestions() {
       return questionrepository.findAll();
    }
    public void addQuestion(Question question) {
        questionrepository.save(question);
    }
    public void deleteQuestion(String id) {
        questionrepository.deleteById(id);
    }
    public void updateQuestion(Question question) {
        questionrepository.save(question);
    }
    public List<Question> getQuestionsByCategory(String category) {
        return questionrepository.findByCategoryIgnoreCase(category);
    }
    public Question getQuestionById(String id) {
        return questionrepository.findById(id).orElse(null);
    }
    public List<Question> getQuestionsByListOfIds(List<String> ids) {
        return questionrepository.findAllById(ids);
    }
    public List<Question> getQuestionsByDifficulty(Difficulty difficulty) {
        return questionrepository.findByDifficulty(difficulty);
    }
    public List<Question> getQuestionsByCategoryAndDifficulty(String category, Difficulty difficulty) {
        return questionrepository.findByCategoryIgnoreCaseAndDifficulty(category, difficulty);
    }

    public String addQuestionsBulk(List<Question> questions) {
        questionrepository.saveAll(questions);
        return "Successfully added " + questions.size() + " questions.";
    }
}
