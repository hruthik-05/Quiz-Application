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

    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    public List<Question> getRandomQuestions(String category, Difficulty difficulty, int limit) {
        List<org.springframework.data.mongodb.core.aggregation.AggregationOperation> operations = new java.util.ArrayList<>();
        List<org.springframework.data.mongodb.core.query.Criteria> criteriaList = new java.util.ArrayList<>();

        if (category != null && !category.equalsIgnoreCase("ALL")) {
            criteriaList.add(org.springframework.data.mongodb.core.query.Criteria.where("category")
                .regex("^" + java.util.regex.Pattern.quote(category) + "$", "i"));
        }

        if (difficulty != null) {
            criteriaList.add(org.springframework.data.mongodb.core.query.Criteria.where("difficulty").is(difficulty));
        }

        if (!criteriaList.isEmpty()) {
            operations.add(org.springframework.data.mongodb.core.aggregation.Aggregation.match(
                new org.springframework.data.mongodb.core.query.Criteria().andOperator(criteriaList.toArray(new org.springframework.data.mongodb.core.query.Criteria[0]))
            ));
        }

        operations.add(org.springframework.data.mongodb.core.aggregation.Aggregation.sample(limit));

        org.springframework.data.mongodb.core.aggregation.Aggregation aggregation = 
            org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, "questions", Question.class).getMappedResults();
    }
    public List<Question> getAllQuestions() {
       return questionrepository.findAll();
    }
    @org.springframework.cache.annotation.CacheEvict(value = "questions", allEntries = true)
    public void addQuestion(Question question) {
        questionrepository.save(question);
    }
    
    @org.springframework.cache.annotation.CacheEvict(value = "questions", key = "#id")
    public void deleteQuestion(String id) {
        questionrepository.deleteById(id);
    }
    
    @org.springframework.cache.annotation.CacheEvict(value = "questions", key = "#question.id")
    public void updateQuestion(Question question) {
        questionrepository.save(question);
    }
    
    public List<Question> getQuestionsByCategory(String category) {
        return questionrepository.findByCategoryIgnoreCase(category);
    }
    
    @org.springframework.cache.annotation.Cacheable(value = "questions", key = "#id")
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

    @org.springframework.cache.annotation.CacheEvict(value = "questions", allEntries = true)
    public String addQuestionsBulk(List<Question> questions) {
        questionrepository.saveAll(questions);
        return "Successfully added " + questions.size() + " questions.";
    }
}
