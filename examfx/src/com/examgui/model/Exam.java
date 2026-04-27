package com.examgui.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

public class Exam {
    private String id;
    private String title, description;
    private int durationMinutes, passingScore;
    private boolean published;
    private final List<Question> questions = new ArrayList<>();
    private final LocalDateTime createdAt = LocalDateTime.now();
    private String createdByUserId;

    public Exam(String title, String description, int durationMinutes, int passingScore, String createdBy) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.createdByUserId = createdBy;
        this.published = false;
    }

    // Constructor for loading from database
    public Exam(String id, String title, String description, int durationMinutes, int passingScore, boolean published, String createdByUserId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
        this.published = published;
        this.createdByUserId = createdByUserId;
        // Note: createdAt is final, so we can't set it. For MongoDB, we might need to adjust.
    }

    public String getId()             { return id; }
    public void setId(String id)       { this.id = id; }
    public String getTitle()          { return title; }
    public String getDescription()    { return description; }
    public int getDurationMinutes()   { return durationMinutes; }
    public int getPassingScore()      { return passingScore; }
    public boolean isPublished()      { return published; }
    public List<Question> getQuestions() { return questions; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public String getCreatedByUserId()   { return createdByUserId; }

    public void setTitle(String t)        { this.title = t; }
    public void setDescription(String d)  { this.description = d; }
    public void setDurationMinutes(int m) { this.durationMinutes = m; }
    public void setPassingScore(int p)    { this.passingScore = p; }
    public void setPublished(boolean p)   { this.published = p; }

    public void addQuestion(Question q)    { questions.add(q); }
    public void removeQuestion(Question q) { questions.remove(q); }

    public int getTotalMarks() {
        return questions.stream().mapToInt(Question::getMarks).sum();
    }
}
