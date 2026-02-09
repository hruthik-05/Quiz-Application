package com.projectquiz.demo.models;
import java.util.List;

public class QuestionDto {
    String id;
    int points;
    String question;
    List<String> options;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getPoints() {
        return points;
    }
    public void setPoints(int  points) {
        this.points = points;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
}
