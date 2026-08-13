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
@RequestMapping("/api/question")
public class QuestionController{
    @Autowired
    QuestionService qService;

    private QuestionDto convertToDto(Question q) {
        if (q == null) return null;
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getId());
        dto.setPoints(q.getPoints());
        dto.setQuestion(q.getQuestionText());
        dto.setOptions(q.getOptions());
        return dto;
    }

    @GetMapping("/allQuestions")
    public List<QuestionDto> getAllQuestions(){
        return qService.getAllQuestions().stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList());
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
    public List<QuestionDto> getQuestionsByCategory(@PathVariable String category){ 
        return qService.getQuestionsByCategory(category).stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList());
    }
    @GetMapping("/getQuestionById/{id}")
    public QuestionDto getQuestionById(@PathVariable String id){
        return convertToDto(qService.getQuestionById(id));
    }
    @PostMapping("/getQuestionsByListOfIds")
    public List<QuestionDto> getQuestionsByListOfIds(@RequestBody List<String> ids){
        return qService.getQuestionsByListOfIds(ids).stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList());
    }
    @GetMapping("/getQuestionsByDifficulty/{difficulty}")
    public List<QuestionDto> getQuestionsByDifficulty(@PathVariable Difficulty difficulty){
        return qService.getQuestionsByDifficulty(difficulty).stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList());
    }
    @GetMapping("/{category}/{difficulty}")
    public List<QuestionDto> getQuestionsByCategoryAndDifficulty(@PathVariable String category, @PathVariable Difficulty difficulty){
        return qService.getQuestionsByCategoryAndDifficulty(category, difficulty).stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/bulk")
    public String addQuestionsBulK(@RequestBody List<Question> questions) {
        return qService.addQuestionsBulk(questions);
    }
}

