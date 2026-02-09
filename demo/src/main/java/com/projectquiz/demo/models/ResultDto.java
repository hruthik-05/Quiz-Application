package com.projectquiz.demo.models;

public class ResultDto {
    private int totalScore;
    private int correct;
    private int wrong;
    private int skipped;
    private long timeTaken;
    private int penalty;

    public int getTotalScore() {
        return totalScore;
    }
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    public int getCorrect() {
        return correct;
    }
    public void setCorrect(int correct) {
        this.correct = correct;
    }
    public int getWrong() {
        return wrong;
    }
    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
    public int getSkipped() {
        return skipped;
    }
    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
    public long getTimeTaken() {
        return timeTaken;
    }
    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }
    public int getPenalty() {
        return penalty;
    }
    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }
}
