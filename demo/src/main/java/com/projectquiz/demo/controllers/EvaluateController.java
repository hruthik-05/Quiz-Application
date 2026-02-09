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
    @RequestMapping("/pointsBasedEval")
    public ResultDto pointsBasedEval(@RequestBody UserResponse userResponse){
        return eService.pointsBasedEvaluation(userResponse);
    }
    @RequestMapping("/timeBasedEval")
    public ResultDto timeBasedEval(@RequestBody UserResponse userResponse){
       return eService.timeBasedEval(userResponse);
    }
}
