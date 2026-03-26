package com.examgui.model;

public class Question {
    private static int nextId = 1;
    private final int id;
    private String text;
    private String optionA, optionB, optionC, optionD;
    private char correctAnswer; // 'A','B','C','D'
    private int marks;

    public Question(String text, String a, String b, String c, String d, char correct, int marks) {
        this.id = nextId++;
        this.text = text;
        this.optionA = a; this.optionB = b;
        this.optionC = c; this.optionD = d;
        this.correctAnswer = Character.toUpperCase(correct);
        this.marks = marks;
    }

    public int getId()            { return id; }
    public String getText()       { return text; }
    public String getOptionA()    { return optionA; }
    public String getOptionB()    { return optionB; }
    public String getOptionC()    { return optionC; }
    public String getOptionD()    { return optionD; }
    public char getCorrectAnswer(){ return correctAnswer; }
    public int getMarks()         { return marks; }

    public String getOption(char letter) {
        return switch (Character.toUpperCase(letter)) {
            case 'A' -> optionA; case 'B' -> optionB;
            case 'C' -> optionC; case 'D' -> optionD;
            default  -> "Unknown";
        };
    }

    public void setText(String t)      { this.text = t; }
    public void setOptionA(String a)   { this.optionA = a; }
    public void setOptionB(String b)   { this.optionB = b; }
    public void setOptionC(String c)   { this.optionC = c; }
    public void setOptionD(String d)   { this.optionD = d; }
    public void setCorrectAnswer(char c){ this.correctAnswer = c; }
    public void setMarks(int m)        { this.marks = m; }
}
