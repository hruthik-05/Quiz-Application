package com.projectquiz.demo.services;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Question;
import com.projectquiz.demo.models.UserResponse;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.services.AnalyticsService;
@Service
public class EvaluationService {
    @Autowired
    QuestionService qService;
    @Autowired
    AnalyticsService analyticsService;
    
   public ResultDto pointsBasedEvaluation(UserResponse userResponse) {

    int totalScore = 0;
    int correct = 0;
    int skipped = 0;

    Map<String, String> responses = userResponse.getResponses();

    for (Map.Entry<String, String> entry : responses.entrySet()) {

        String questionId = entry.getKey();
        String userAnswer = entry.getValue();

        Question question = qService.getQuestionById(questionId);

        if (question == null) {
            skipped++;
            continue;
        }

        if (question.getAnswer().equalsIgnoreCase(userAnswer)) {
            correct++;
            totalScore += question.getPoints();
        }
    }

    ResultDto result = new ResultDto();
    result.setTotalScore(totalScore);
    result.setCorrect(correct);
    result.setSkipped(skipped);
    result.setWrong(responses.size() - correct - skipped);
    result.setTimeTaken(userResponse.getEndTime() - userResponse.getStartTime());
    result.setPenalty(0);

    if (userResponse.getUserId() != null && userResponse.getSubject() != null) {
        analyticsService.updateStats(userResponse.getUserId(), userResponse.getSubject(), result);
    }

    return result;
}
public ResultDto timeBasedEval(UserResponse userResponse) {


    ResultDto result = pointsBasedEvaluation(userResponse);

    long timeTaken = result.getTimeTaken();
    long timeLimit = userResponse.getTimeLimit();

    int penalty = 0;

    if (timeLimit > 0 && timeTaken > timeLimit) {




        penalty = (int) ((timeTaken - timeLimit) / 10);
    }


    int finalScore = result.getTotalScore() - penalty;

    result.setPenalty(penalty);
    result.setTotalScore(Math.max(finalScore, 0));

    return result;
}
}

