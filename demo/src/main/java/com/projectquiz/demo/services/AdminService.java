package com.projectquiz.demo.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.repositories.QuestionRepository;

@Service
public class AdminService {

    @Autowired
    QuestionRepository questionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    private void validateQuestion(Question q) {
        if (q.getQuestionText() == null || q.getQuestionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }
        if (q.getOptions() == null || q.getOptions().size() < 2) {
            throw new IllegalArgumentException("Question must have at least 2 options");
        }
        for (String opt : q.getOptions()) {
            if (opt == null || opt.trim().isEmpty()) {
                throw new IllegalArgumentException("Option cannot be empty");
            }
        }
        if (q.getPoints() <= 0) {
            throw new IllegalArgumentException("Points must be a positive number");
        }
        if (q.getAnswer() == null || q.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Correct answer is required");
        }
        boolean matchesOption = q.getOptions().stream()
            .anyMatch(opt -> opt != null && opt.trim().equalsIgnoreCase(q.getAnswer().trim()));
        if (!matchesOption) {
            throw new IllegalArgumentException("Correct answer must match one of the options");
        }
        if (q.getCategory() == null || q.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
    }

    @CacheEvict(value = "questions", allEntries = true)
    public Question createQuestion(Question question) {
        validateQuestion(question);
        return questionRepository.save(question);
    }

    @CacheEvict(value = "questions", allEntries = true)
    public List<Question> createQuestionsBatch(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions batch cannot be empty");
        }
        for (Question q : questions) {
            validateQuestion(q);
        }
        return questionRepository.saveAll(questions);
    }

    @Caching(evict = {
        @CacheEvict(value = "questions", key = "#id"),
        @CacheEvict(value = "questions", allEntries = true)
    })
    public boolean deleteQuestion(String id) {
        if (!questionRepository.existsById(id)) {
            return false;
        }
        questionRepository.deleteById(id);
        return true;
    }
}
