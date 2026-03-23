package com.examgui.model;

import java.time.LocalDateTime;
import java.util.*;

public class ExamAttempt {
    public enum Status { IN_PROGRESS, SUBMITTED, TIMED_OUT }

    private static int nextId = 1;
    private final int id;
    private final int studentId;
    private final int examId;
    private final String examTitle;
    private final LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime submittedAt;
    private Status status = Status.IN_PROGRESS;

    // questionId → selected answer ('A','B','C','D' or 0=skipped)
    private final Map<Integer, Character> answers = new LinkedHashMap<>();

    private int score, totalMarks;
    private double percentage;
    private boolean passed;

    public ExamAttempt(int studentId, int examId, String examTitle) {
        this.id = nextId++;
        this.studentId = studentId;
        this.examId = examId;
        this.examTitle = examTitle;
    }

    public void recordAnswer(int questionId, char answer) {
        answers.put(questionId, answer);
    }

    public Character getAnswer(int questionId) {
        return answers.get(questionId);
    }

    public void submit(int score, int totalMarks, boolean passed) {
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        this.passed = passed;
        this.status = Status.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters
    public int getId()              { return id; }
    public int getStudentId()       { return studentId; }
    public int getExamId()          { return examId; }
    public String getExamTitle()    { return examTitle; }
    public LocalDateTime getStartedAt()   { return startedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public Status getStatus()       { return status; }
    public int getScore()           { return score; }
    public int getTotalMarks()      { return totalMarks; }
    public double getPercentage()   { return percentage; }
    public boolean isPassed()       { return passed; }
    public Map<Integer, Character> getAnswers() { return answers; }
    public void setStatus(Status s) { this.status = s; }
}
