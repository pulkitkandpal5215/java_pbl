package com.examgui.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

public class ExamAttempt {
    public enum Status { IN_PROGRESS, SUBMITTED, TIMED_OUT }

    private String id;
    private final String studentId;
    private final String examId;
    private final String examTitle;
    private final LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime submittedAt;
    private Status status = Status.IN_PROGRESS;

    private final Map<String, Character> answers = new LinkedHashMap<>();

    private int score, totalMarks;
    private double percentage;
    private boolean passed;

    public ExamAttempt(String studentId, String examId, String examTitle) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.examId = examId;
        this.examTitle = examTitle;
    }

    // Constructor for loading from database
    public ExamAttempt(String id, String studentId, String examId, String examTitle, LocalDateTime startedAt, LocalDateTime submittedAt, Status status, Map<String, Character> answers, int score, int totalMarks, double percentage, boolean passed) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.examTitle = examTitle;
        // startedAt is final, can't set
        this.submittedAt = submittedAt;
        this.status = status;
        this.answers.putAll(answers);
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.passed = passed;
    }

    public void recordAnswer(String questionId, char answer) { answers.put(questionId, answer); }
    public Character getAnswer(String questionId) { return answers.get(questionId); }

    public void submit(int score, int totalMarks, boolean passed) {
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        this.passed = passed;
        this.status = Status.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    public void timeout(int score, int totalMarks, boolean passed) {
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        this.passed = passed;
        this.status = Status.TIMED_OUT;
        this.submittedAt = LocalDateTime.now();
    }

    public String getId()            { return id; }
    public void setId(String id)      { this.id = id; }
    public String getStudentId()     { return studentId; }
    public String getExamId()        { return examId; }
    public String getExamTitle()     { return examTitle; }
    public LocalDateTime getStartedAt()   { return startedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public Status getStatus()         { return status; }
    public int getScore()             { return score; }
    public int getTotalMarks()      { return totalMarks; }
    public double getPercentage()   { return percentage; }
    public boolean isPassed()       { return passed; }
    public Map<String, Character> getAnswers() { return answers; }
    public void setStatus(Status s) { this.status = s; }
}
