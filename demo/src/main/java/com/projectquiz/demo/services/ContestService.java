package com.projectquiz.demo.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

import com.projectquiz.demo.models.Contest;
import com.projectquiz.demo.models.ContestAttempt;
import com.projectquiz.demo.models.ContestAttemptDto;
import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.repositories.ContestAttemptRepository;
import com.projectquiz.demo.repositories.ContestRepository;
import com.projectquiz.demo.repositories.QuestionRepository;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class ContestService {

    @Autowired
    ContestRepository contestRepository;

    @Autowired
    ContestAttemptRepository attemptRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @jakarta.annotation.PostConstruct
    public void initIndexes() {
        try {
            contestRepository.count(); 
            var indexOps = mongoTemplate.indexOps(ContestAttempt.class);
            indexOps.ensureIndex(new org.springframework.data.mongodb.core.index.CompoundIndexDefinition(
                new org.bson.Document("userId", 1)
                    .append("contestId", 1)
                    .append("attemptNumber", 1)
            ).unique().named("user_contest_attempt_idx"));
        } catch (Exception e) {
            System.err.println("Failed to ensure contest_attempts unique index: " + e.getMessage());
        }
    }

    @CacheEvict(value = {"contests", "contests_all", "contests_active", "contest_questions"}, allEntries = true)
    public Contest createContest(Contest contest) {
        if (contest.getContestQuestions() != null) {
            List<String> qIds = new ArrayList<>();
            for (Question q : contest.getContestQuestions()) {
                if (q.getId() == null || q.getId().trim().isEmpty()) {
                    q.setId(UUID.randomUUID().toString());
                }
                qIds.add(q.getId());
            }
            contest.setQuestionIds(qIds);
        }
        return contestRepository.save(contest);
    }

    @Cacheable(value = "contests_all")
    public List<Contest> getAllContests() {
        List<Contest> contests = contestRepository.findAll();
        for (Contest c : contests) {
            publishContestQuestionsIfEnded(c);
        }
        return contests;
    }
    
    @Cacheable(value = "contests_active")
    public List<Contest> getActiveContests() {
        List<Contest> contests = contestRepository.findByIsActiveTrue();
        for (Contest c : contests) {
            publishContestQuestionsIfEnded(c);
        }
        return contests;
    }

    @Cacheable(value = "contests", key = "#id")
    public Contest getContestById(String id) {
        return contestRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "contest_questions", key = "#contestId")
    public List<Question> getQuestionsForContest(String contestId) {
        long now = System.currentTimeMillis();
        
        Contest contest = contestRepository.findById(contestId).orElse(null);
        
        if (contest == null) {
            return List.of();
        }
        if(now<contest.getEndTime()) return List.of();
        publishContestQuestionsIfEnded(contest);
        return contest.getContestQuestions() != null ? contest.getContestQuestions() : List.of();
    }

    public ContestAttempt submitContest(String contestId, String userId, Map<String, String> responses, long timeTaken) {

        Contest contest = getContestById(contestId);
        if (contest == null) {
            throw new RuntimeException("Contest not found");
        }


        int attemptsCount = attemptRepository.countByUserIdAndContestId(userId, contestId);
        if (attemptsCount >= contest.getMaxAttempts()) {
             throw new RuntimeException("Maximum attempts reached for this contest!");
        }
        

        long now = System.currentTimeMillis();


        if (now < contest.getStartTime()) {
             throw new RuntimeException("Contest has not started yet.");
        }
        if (now > contest.getEndTime() + (5 * 60 * 1000)) { 
             throw new RuntimeException("Contest submission window closed.");
        }

        long maxPossibleTime = now - contest.getStartTime();
        if (timeTaken < 0 || timeTaken > maxPossibleTime) {
            timeTaken = maxPossibleTime;
        }

        double score = 0;
        int correct = 0;
        int wrong = 0;

        List<Question> contestQuestions = contest.getContestQuestions();
        if (contestQuestions == null) {
            contestQuestions = List.of();
        }
        Map<String, Question> contestQuestionsMap = contestQuestions.stream()
            .collect(Collectors.toMap(Question::getId, q -> q));

        for (Map.Entry<String, String> entry : responses.entrySet()) {
            String questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Question q = contestQuestionsMap.get(questionId);
            if (q != null) {
                if (q.getAnswer().trim().equalsIgnoreCase(userAnswer.trim())) {
                    score += 1.0; 
                    correct++;
                } else {
                    wrong++;
                    if (contest.isAllowNegativeMarking()) {
                        score -= contest.getNegativeMarkFactor();
                    }
                }
            }
        }
        

        ContestAttempt attempt = new ContestAttempt();
        attempt.setContestId(contestId);
        attempt.setUserId(userId);
        attempt.setResponses(responses);
        attempt.setScore(score);
        attempt.setTimeTaken(timeTaken);
        attempt.setSubmittedAt(now);
        attempt.setAttemptNumber(attemptsCount + 1);

        ContestAttempt savedAttempt;
        try {
            savedAttempt = attemptRepository.save(attempt);
        } catch (com.mongodb.MongoWriteException | org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("Maximum attempts reached for this contest!");
        }

        if (now > contest.getEndTime()) {
            java.util.Map<String, String> correctAnswers = new java.util.HashMap<>();
            for (Question q : contestQuestions) {
                correctAnswers.put(q.getId(), q.getAnswer());
            }
            savedAttempt.setCorrectAnswers(correctAnswers);
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                String emailBody = String.format(
                    "Hi %s,\n\nYou have completed the contest: %s.\n\nScore: %.2f\nCorrect: %d\nWrong: %d\n\nKeep improving!",
                    user.getUsername(), contest.getTitle(), score, correct, wrong
                );
                emailService.sendQuizResult(user.getEmail(), "Contest Result: " + contest.getTitle(), emailBody);
            }
        }

        return savedAttempt;
    }
    public List<ContestAttemptDto> getAttemptsForContest(String contestId) {
        List<ContestAttempt> attempts = attemptRepository.findByContestId(contestId);
        List<String> userIds = attempts.stream().map(ContestAttempt::getUserId).distinct().toList();
        List<User> users = (List<User>) userRepository.findAllById(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));
        Contest contest = contestRepository.findById(contestId).orElse(null);
        if (contest != null) {
            publishContestQuestionsIfEnded(contest);
        }
        String contestTitle = contest != null ? contest.getTitle() : "Unknown Contest";
        long now = System.currentTimeMillis();

        return attempts.stream().map(a -> {
            ContestAttemptDto dto = new ContestAttemptDto();
            dto.setId(a.getId());
            dto.setContestId(a.getContestId());
            dto.setContestTitle(contestTitle);
            dto.setUserId(a.getUserId());
            dto.setScore(a.getScore());
            dto.setTimeTaken(a.getTimeTaken());
            dto.setSubmittedAt(a.getSubmittedAt());
            dto.setResponses(a.getResponses());

            if (contest != null && now > contest.getEndTime()) {
                java.util.Map<String, String> correctAnswers = new java.util.HashMap<>();
                if (contest.getContestQuestions() != null) {
                    for (Question q : contest.getContestQuestions()) {
                        correctAnswers.put(q.getId(), q.getAnswer());
                    }
                }
                dto.setCorrectAnswers(correctAnswers);
            }

            User u = userMap.get(a.getUserId());
            if (u != null) {
                dto.setUsername(u.getUsername());
                dto.setEmail(u.getEmail());
            } else {
                dto.setUsername("Unknown User");
            }
            return dto;
        }).toList();
    }
    
    public List<ContestAttempt> getStudentAttempts(String userId) {
        List<ContestAttempt> attempts = attemptRepository.findByUserId(userId);
        long now = System.currentTimeMillis();
        for (ContestAttempt attempt : attempts) {
            Contest contest = contestRepository.findById(attempt.getContestId()).orElse(null);
            if (contest != null) {
                publishContestQuestionsIfEnded(contest);
                if (now > contest.getEndTime()) {
                    java.util.Map<String, String> correctAnswers = new java.util.HashMap<>();
                    if (contest.getContestQuestions() != null) {
                        for (Question q : contest.getContestQuestions()) {
                            correctAnswers.put(q.getId(), q.getAnswer());
                        }
                    }
                    attempt.setCorrectAnswers(correctAnswers);
                }
            }
        }
        return attempts;
    }

    public synchronized void publishContestQuestionsIfEnded(Contest contest) {
        if (contest == null || contest.getContestQuestions() == null || contest.getContestQuestions().isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now > contest.getEndTime()) {
            boolean publishedNew = false;
            for (Question q : contest.getContestQuestions()) {
                if (!questionRepository.existsById(q.getId())) {
                    Question pq = new Question();
                    pq.setId(q.getId());
                    pq.setQuestionText(q.getQuestionText());
                    pq.setOptions(q.getOptions());
                    pq.setAnswer(q.getAnswer());
                    pq.setPoints(q.getPoints());
                    pq.setCategory(q.getCategory());
                    pq.setDifficulty(q.getDifficulty());
                    
                    questionRepository.save(pq);
                    publishedNew = true;
                }
                else{
                    break;
                }
            }
            if (publishedNew) {
                evictContestCache(contest.getId());
            }
        }
    }

    public void evictContestCache(String contestId) {
        if (cacheManager != null) {
            try {
                Cache cache = cacheManager.getCache("contests");
                if (cache != null) {
                    cache.evict(contestId);
                }
                Cache qCache = cacheManager.getCache("contest_questions");
                if (qCache != null) {
                    qCache.evict(contestId);
                }
                Cache allCache = cacheManager.getCache("contests_all");
                if (allCache != null) {
                    allCache.clear();
                }
                Cache activeCache = cacheManager.getCache("contests_active");
                if (activeCache != null) {
                    activeCache.clear();
                }
            } catch (Exception e) {
                System.err.println("Failed to evict contest cache: " + e.getMessage());
            }
        }
    }
}
