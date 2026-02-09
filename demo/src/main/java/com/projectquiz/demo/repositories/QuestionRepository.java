package com.projectquiz.demo.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;

public interface QuestionRepository extends MongoRepository<Question,String> {

    List<Question> findByCategoryIgnoreCase(String string);

    List<Question> findByDifficulty(Difficulty difficulty);

    List<Question> findByCategoryIgnoreCaseAndDifficulty(String category, Difficulty difficulty);

    
} 

