package com.projectquiz.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectquiz.demo.models.UserResponse;
import com.projectquiz.demo.models.ResultDto;
import com.projectquiz.demo.services.EvaluationService;

@RestController
@RequestMapping("/evaluate")
public class EvaluateController {
    @Autowired
    EvaluationService eService;
    private String getAuthenticatedUserId() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            Object principal = auth.getPrincipal();
            if (principal instanceof com.projectquiz.demo.security.services.UserDetailsImpl) {
                return ((com.projectquiz.demo.security.services.UserDetailsImpl) principal).getId();
            }
        }
        return null;
    }

    private void sanitizeUserResponse(UserResponse userResponse) {
        userResponse.setUserId(getAuthenticatedUserId());
        long now = System.currentTimeMillis();
        if (userResponse.getStartTime() <= 0 || userResponse.getStartTime() > now) {
            userResponse.setStartTime(now);
        }
        userResponse.setEndTime(now);
    }

    @RequestMapping("/pointsBasedEval")
    public ResultDto pointsBasedEval(@RequestBody UserResponse userResponse){
        sanitizeUserResponse(userResponse);
        return eService.pointsBasedEvaluation(userResponse);
    }
    @RequestMapping("/timeBasedEval")
    public ResultDto timeBasedEval(@RequestBody UserResponse userResponse){
       sanitizeUserResponse(userResponse);
       return eService.timeBasedEval(userResponse);
    }
}
