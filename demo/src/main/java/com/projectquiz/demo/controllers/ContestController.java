package com.projectquiz.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.Contest;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.services.ContestService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/contest")
public class ContestController {

    @Autowired
    ContestService contestService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Contest createContest(@RequestBody Contest contest) {
        return contestService.createContest(contest);
    }

    @GetMapping("/all")
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }
    
    @GetMapping("/{id}")
    public Contest getContest(@PathVariable String id) {
        return contestService.getContestById(id);
    }
    
    @GetMapping("/{id}/questions")
    public List<com.projectquiz.demo.models.Question> getContestQuestions(@PathVariable String id) {
        return contestService.getQuestionsForContest(id);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitContest(@RequestBody Map<String, Object> payload) {
        try {
            String contestId = (String) payload.get("contestId");
            String userId = (String) payload.get("userId");
            long timeTaken = ((Number) payload.get("timeTaken")).longValue();
            @SuppressWarnings("unchecked")
            Map<String, String> responses = (Map<String, String>) payload.get("responses");

            ContestAttempt attempt = contestService.submitContest(contestId, userId, responses, timeTaken);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/attempts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<com.projectquiz.demo.models.ContestAttemptDto> getContestAttempts(@PathVariable String id) {
        return contestService.getAttemptsForContest(id);
    }

    @GetMapping("/my-results/{userId}")
    public List<ContestAttempt> getMyContestResults(@PathVariable String userId) {
        return contestService.getStudentAttempts(userId);
    }
}
