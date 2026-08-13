package com.projectquiz.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.Question;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    com.projectquiz.demo.services.AdminService adminService;

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return adminService.getAllQuestions();
    }

    @PostMapping("/questions")
    public Question createQuestion(@RequestBody Question question) {
        return adminService.createQuestion(question);
    }
    
    @PostMapping("/questions/batch")
    public List<Question> createQuestionsBatch(@RequestBody List<Question> questions) {
        return adminService.createQuestionsBatch(questions);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        boolean deleted = adminService.deleteQuestion(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
