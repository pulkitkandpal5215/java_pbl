package com.examgui.data;

import com.examgui.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 🗄️ DataStore — In-memory database (no file/DB needed)
 * Singleton pattern: one shared instance across the whole app.
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();
    public static DataStore get() { return INSTANCE; }

    private final List<User>        users    = new ArrayList<>();
    private final List<Exam>        exams    = new ArrayList<>();
    private final List<ExamAttempt> attempts = new ArrayList<>();

    /** Currently logged-in user (set on login, cleared on logout) */
    private User currentUser;

    private DataStore() {
        seedData();
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    public User login(String email, String password) {
        return users.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email.trim())
                      && u.getPassword().equals(password))
            .findFirst().orElse(null);
    }

    public boolean registerStudent(String name, String email, String password) {
        if (users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) return false;
        users.add(new User(name, email, password, User.Role.STUDENT));
        return true;
    }

    public void setCurrentUser(User u) { this.currentUser = u; }
    public User getCurrentUser()       { return currentUser; }
    public void logout()               { currentUser = null; }

    // ── Users ─────────────────────────────────────────────────────────────────

    public List<User> getAllStudents() {
        return users.stream().filter(u -> !u.isAdmin()).collect(Collectors.toList());
    }

    public Optional<User> getUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    // ── Exams ─────────────────────────────────────────────────────────────────

    public void addExam(Exam e)    { exams.add(e); }
    public void removeExam(Exam e) { exams.remove(e); }

    public List<Exam> getAllExams()       { return new ArrayList<>(exams); }
    public List<Exam> getPublishedExams() {
        return exams.stream().filter(Exam::isPublished).collect(Collectors.toList());
    }

    public Optional<Exam> getExamById(int id) {
        return exams.stream().filter(e -> e.getId() == id).findFirst();
    }

    // ── Attempts ──────────────────────────────────────────────────────────────

    public void addAttempt(ExamAttempt a)  { attempts.add(a); }

    public List<ExamAttempt> getAttemptsForStudent(int studentId) {
        return attempts.stream()
            .filter(a -> a.getStudentId() == studentId)
            .collect(Collectors.toList());
    }

    public List<ExamAttempt> getAttemptsForExam(int examId) {
        return attempts.stream()
            .filter(a -> a.getExamId() == examId)
            .collect(Collectors.toList());
    }

    public boolean hasActiveAttempt(int studentId, int examId) {
        return attempts.stream().anyMatch(a ->
            a.getStudentId() == studentId &&
            a.getExamId() == examId &&
            a.getStatus() == ExamAttempt.Status.IN_PROGRESS);
    }

    // ── Seed Data ─────────────────────────────────────────────────────────────

    private void seedData() {
        // Users
        User admin   = new User("Admin User",   "admin@exam.com",   "admin123",   User.Role.ADMIN);
        User pulkit   = new User("Pulkit","pulkit@gmail.com",   "pulkit123",   User.Role.STUDENT);
        User vaibhav     = new User("Vaibhav",    "vaibhav@gmail.com",     "vaibhav123",     User.Role.STUDENT);
        users.addAll(List.of(admin, pulkit, vaibhav));

        // ── Exam 1: Java Basics ───────────────────────────────────────────────
        Exam java = new Exam("Java Programming Basics",
            "Test your knowledge of core Java concepts.", 30, 60, admin.getId());

        java.addQuestion(new Question(
            "Which keyword is used to define a class in Java?",
            "class", "define", "struct", "object", 'A', 1));
        java.addQuestion(new Question(
            "What is the default value of an int variable?",
            "null", "0", "undefined", "-1", 'B', 1));
        java.addQuestion(new Question(
            "Which of these is NOT a Java primitive type?",
            "int", "boolean", "String", "char", 'C', 1));
        java.addQuestion(new Question(
            "What does JVM stand for?",
            "Java Virtual Machine", "Java Variable Method",
            "Java Verified Module", "Java Visual Manager", 'A', 2));
        java.addQuestion(new Question(
            "Which method is the entry point of a Java application?",
            "start()", "run()", "main()", "init()", 'C', 2));
        java.addQuestion(new Question(
            "Which operator is used to create an object in Java?",
            "create", "make", "new", "object", 'C', 1));
        java.addQuestion(new Question(
            "What is the size of an int in Java?",
            "8 bits", "16 bits", "32 bits", "64 bits", 'C', 1));
        java.addQuestion(new Question(
            "Which collection allows duplicate elements?",
            "Set", "Map", "List", "TreeSet", 'C', 2));
        java.setPublished(true);

        // ── Exam 2: General Knowledge ─────────────────────────────────────────
        Exam gk = new Exam("General Knowledge Quiz",
            "A fun quiz on science, history, and world facts.", 20, 50, admin.getId());

        gk.addQuestion(new Question(
            "What is the chemical symbol for water?",
            "WA", "H2O", "HO", "W2O", 'B', 1));
        gk.addQuestion(new Question(
            "Which planet is closest to the Sun?",
            "Venus", "Earth", "Mercury", "Mars", 'C', 1));
        gk.addQuestion(new Question(
            "In which year did World War II end?",
            "1943", "1944", "1945", "1946", 'C', 1));
        gk.addQuestion(new Question(
            "Who wrote 'Romeo and Juliet'?",
            "Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain", 'B', 1));
        gk.addQuestion(new Question(
            "What is the capital of Japan?",
            "Beijing", "Seoul", "Bangkok", "Tokyo", 'D', 1));
        gk.addQuestion(new Question(
            "How many continents are there on Earth?",
            "5", "6", "7", "8", 'C', 1));
        gk.setPublished(true);

        // ── Exam 3: Math Quiz ─────────────────────────────────────────────────
        Exam math = new Exam("Mathematics Fundamentals",
            "Basic algebra, arithmetic, and geometry.", 25, 70, admin.getId());

        math.addQuestion(new Question(
            "What is the value of π (pi) approximately?",
            "2.718", "3.141", "1.414", "1.732", 'B', 1));
        math.addQuestion(new Question(
            "What is 15% of 200?",
            "25", "30", "35", "40", 'B', 1));
        math.addQuestion(new Question(
            "Solve: 2x + 5 = 15. What is x?",
            "3", "4", "5", "6", 'C', 2));
        math.addQuestion(new Question(
            "What is the area of a circle with radius 7? (Use π ≈ 22/7)",
            "144", "154", "164", "174", 'B', 2));
        math.addQuestion(new Question(
            "What is the square root of 144?",
            "11", "12", "13", "14", 'B', 1));
        math.setPublished(true);

        // ── Exam 4: Draft (unpublished) ───────────────────────────────────────
        Exam draft = new Exam("Advanced Java (Draft)",
            "Not published yet.", 60, 75, admin.getId());
        draft.addQuestion(new Question(
            "What is a lambda expression?",
            "A loop", "An anonymous function", "A class", "A variable", 'B', 2));

        exams.addAll(List.of(java, gk, math, draft));

        // ── Sample completed attempts ─────────────────────────────────────────
        ExamAttempt a1 = new ExamAttempt(pulkit.getId(), java.getId(), java.getTitle());
        a1.recordAnswer(1, 'A'); a1.recordAnswer(2, 'B'); a1.recordAnswer(3, 'C');
        a1.recordAnswer(4, 'A'); a1.recordAnswer(5, 'C'); a1.recordAnswer(6, 'C');
        a1.recordAnswer(7, 'C'); a1.recordAnswer(8, 'C');
        a1.submit(9, java.getTotalMarks(), true);
        attempts.add(a1);

        ExamAttempt a2 = new ExamAttempt(vaibhav.getId(), gk.getId(), gk.getTitle());
        a2.recordAnswer(9, 'B'); a2.recordAnswer(10, 'C'); a2.recordAnswer(11, 'C');
        a2.recordAnswer(12, 'B'); a2.recordAnswer(13, 'D'); a2.recordAnswer(14, 'C');
        a2.submit(6, gk.getTotalMarks(), true);
        attempts.add(a2);
    }
}
