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
import com.projectquiz.demo.models.QuestionDto;
import com.projectquiz.demo.models.Contest;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.models.ContestAttemptDto;
import com.projectquiz.demo.models.Question;
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
        if (contest.getTitle() == null || contest.getTitle().trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Contest title is required");
        }
        if (contest.getDurationMinutes() <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Contest duration must be positive");
        }
        if (contest.isAllowNegativeMarking() && contest.getNegativeMarkFactor() < 0) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Negative marking factor cannot be negative");
        }
        if (contest.getStartTime() <= 0 || contest.getEndTime() <= contest.getStartTime()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid contest start or end time");
        }
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
    public List<com.projectquiz.demo.models.QuestionDto> getContestQuestions(@PathVariable String id) {
        List<Question> questions = contestService.getQuestionsForContest(id);
        return questions.stream().map(q -> {
            QuestionDto dto = new QuestionDto();
            dto.setId(q.getId());
            dto.setPoints(q.getPoints());
            dto.setQuestion(q.getQuestionText());
            dto.setOptions(q.getOptions());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitContest(@RequestBody Map<String, Object> payload) {
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }
            com.projectquiz.demo.security.services.UserDetailsImpl userDetails = (com.projectquiz.demo.security.services.UserDetailsImpl) auth.getPrincipal();
            String userId = userDetails.getId();

            String contestId = (String) payload.get("contestId");
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
    public List<ContestAttemptDto> getContestAttempts(@PathVariable String id) {
        return contestService.getAttemptsForContest(id);
    }

    @GetMapping("/my-results/{userId}")
    public List<ContestAttempt> getMyContestResults(@PathVariable String userId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        com.projectquiz.demo.security.services.UserDetailsImpl userDetails = (com.projectquiz.demo.security.services.UserDetailsImpl) auth.getPrincipal();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !userDetails.getId().equals(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access denied");
        }
        return contestService.getStudentAttempts(userId);
    }
}
