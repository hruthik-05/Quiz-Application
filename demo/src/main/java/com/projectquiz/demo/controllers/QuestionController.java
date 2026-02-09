package com.projectquiz.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.Difficulty;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.services.QuestionService;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/question")
public class QuestionController{
    @Autowired
    QuestionService qService;

    @GetMapping("/allQuestions")
    public List<Question> getAllQuestions(){
        return qService.getAllQuestions();
    }
    @PostMapping("/addQuestion")
    public String addQuestion(@RequestBody Question question){
        qService.addQuestion(question); 
        return "added";
    }

    @DeleteMapping("/deleteQuestion/{id}")
    public void deleteQuestion(@PathVariable String id){
        qService.deleteQuestion(id);
    }

    @PutMapping("/updateQuestion")
    public void updateQuestion(@RequestBody Question question){
        qService.updateQuestion(question);
    }
    @GetMapping("/getQuestionsByCategory/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category){ 
        return qService.getQuestionsByCategory(category);
    }
    @GetMapping("/getQuestionById/{id}")
    public Question getQuestionById(@PathVariable String id){
        return qService.getQuestionById(id);
    }
    @GetMapping("/getQuestionsByListOfIds")
    public List<Question> getQuestionsByListOfIds(@RequestBody List<String> ids){
        return qService.getQuestionsByListOfIds(ids);
    }
    @GetMapping("/getQuestionsByDifficulty/{difficulty}")
    public List<Question> getQuestionsByDifficulty(@PathVariable Difficulty difficulty){
        return qService.getQuestionsByDifficulty(difficulty);
    }
    @GetMapping("/{category}/{difficulty}")
    public List<Question> getQuestionsByCategoryAndDifficulty(@PathVariable String category, @PathVariable Difficulty difficulty){
        return qService.getQuestionsByCategoryAndDifficulty(category, difficulty);
    }

    @PostMapping("/bulk")
    public String addQuestionsBulK(@RequestBody List<Question> questions) {
        return qService.addQuestionsBulk(questions);
    }
}

