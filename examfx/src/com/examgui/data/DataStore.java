package com.examgui.data;

import com.examgui.model.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DataStore — MongoDB database integration.
 */
public class DataStore {

    private static final DataStore INSTANCE = new DataStore();
    public static DataStore get() { return INSTANCE; }

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;
    private MongoCollection<Document> examsCollection;
    private MongoCollection<Document> attemptsCollection;

    private User currentUser;

    private DataStore() {
        System.out.println("DataStore constructor called");
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("examfx");
            usersCollection = database.getCollection("users");
            examsCollection = database.getCollection("exams");
            attemptsCollection = database.getCollection("attempts");
            System.out.println("DataStore initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            System.err.println("Falling back to in-memory storage.");
            // Could implement fallback to in-memory if needed
            throw new RuntimeException("MongoDB connection failed", e);
        }
    }

    // ── Auth ──────────────────────────────────────────────────────────────────

    public User login(String email, String password) {
        System.out.println("Login attempt: " + email + " / " + password);
        Document doc = usersCollection.find(Filters.and(
            Filters.eq("email", email.trim().toLowerCase()),
            Filters.eq("password", password)
        )).first();
        if (doc != null) {
            System.out.println("User found: " + doc.getString("name"));
            return documentToUser(doc);
        }
        System.out.println("User not found");
        return null;
    }

    public boolean registerStudent(String name, String email, String password) {
        Document existing = usersCollection.find(Filters.eq("email", email.toLowerCase())).first();
        if (existing != null) return false;
        
        Document doc = new Document()
            .append("_id", new ObjectId())
            .append("name", name)
            .append("email", email.toLowerCase())
            .append("password", password)
            .append("role", "STUDENT");
        usersCollection.insertOne(doc);
        return true;
    }

    public void setCurrentUser(User u) { this.currentUser = u; }
    public User getCurrentUser()       { return currentUser; }
    public void logout()               { currentUser = null; }

    // ── Users ─────────────────────────────────────────────────────────────────

    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        for (Document doc : usersCollection.find(Filters.eq("role", "STUDENT"))) {
            students.add(documentToUser(doc));
        }
        return students;
    }

    public Optional<User> getUserById(String id) {
        try {
            Document doc = usersCollection.find(Filters.eq("_id", new ObjectId(id))).first();
            return doc != null ? Optional.of(documentToUser(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ── Exams ─────────────────────────────────────────────────────────────────

    public void addExam(Exam e) {
        Document doc = examToDocument(e);
        examsCollection.insertOne(doc);
        // Update the exam ID with the generated ObjectId
        e.setId(doc.getObjectId("_id").toHexString());
    }

    public void updateExam(Exam e) {
        try {
            Document doc = examToDocument(e);
            examsCollection.replaceOne(Filters.eq("_id", new ObjectId(e.getId())), doc);
        } catch (IllegalArgumentException ex) {
            // Invalid ID, ignore
        }
    }

    public void removeExam(Exam e) {
        try {
            examsCollection.deleteOne(Filters.eq("_id", new ObjectId(e.getId())));
        } catch (IllegalArgumentException ex) {
            // Invalid ID, ignore
        }
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        for (Document doc : examsCollection.find()) {
            exams.add(documentToExam(doc));
        }
        return exams;
    }

    public List<Exam> getPublishedExams() {
        List<Exam> exams = new ArrayList<>();
        for (Document doc : examsCollection.find(Filters.eq("published", true))) {
            exams.add(documentToExam(doc));
        }
        return exams;
    }

    public Optional<Exam> getExamById(String id) {
        try {
            Document doc = examsCollection.find(Filters.eq("_id", new ObjectId(id))).first();
            return doc != null ? Optional.of(documentToExam(doc)) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ── Attempts ──────────────────────────────────────────────────────────────

    public void addAttempt(ExamAttempt a) {
        Document doc = attemptToDocument(a);
        attemptsCollection.insertOne(doc);
        a.setId(doc.getObjectId("_id").toHexString());
    }

    public void updateAttempt(ExamAttempt a) {
        try {
            Document doc = attemptToDocument(a);
            attemptsCollection.replaceOne(Filters.eq("_id", new ObjectId(a.getId())), doc);
        } catch (IllegalArgumentException ex) {
            // Invalid ID, ignore
        }
    }

    public List<ExamAttempt> getAttemptsForStudent(String studentId) {
        List<ExamAttempt> attempts = new ArrayList<>();
        for (Document doc : attemptsCollection.find(Filters.eq("studentId", studentId))) {
            attempts.add(documentToAttempt(doc));
        }
        return attempts;
    }

    public List<ExamAttempt> getAttemptsForExam(String examId) {
        List<ExamAttempt> attempts = new ArrayList<>();
        for (Document doc : attemptsCollection.find(Filters.eq("examId", examId))) {
            attempts.add(documentToAttempt(doc));
        }
        return attempts;
    }

    public List<ExamAttempt> getAllAttempts() {
        List<ExamAttempt> attempts = new ArrayList<>();
        for (Document doc : attemptsCollection.find()) {
            attempts.add(documentToAttempt(doc));
        }
        return attempts;
    }

    public boolean hasActiveAttempt(String studentId, String examId) {
        Document doc = attemptsCollection.find(Filters.and(
            Filters.eq("studentId", studentId),
            Filters.eq("examId", examId),
            Filters.eq("status", "IN_PROGRESS")
        )).first();
        return doc != null;
    }

    // ── Helper Methods ─────────────────────────────────────────────────────────

    private User documentToUser(Document doc) {
        String id = doc.getObjectId("_id").toHexString();
        String name = doc.getString("name");
        String email = doc.getString("email");
        String password = doc.getString("password");
        String roleStr = doc.getString("role");
        User.Role role = "ADMIN".equals(roleStr) ? User.Role.ADMIN : User.Role.STUDENT;
        User user = new User(name, email, password, role);
        user.setId(id);
        return user;
    }

    private Document userToDocument(User user) {
        return new Document()
            .append("name", user.getName())
            .append("email", user.getEmail().toLowerCase())
            .append("password", user.getPassword())
            .append("role", user.getRole().toString());
    }

    private Exam documentToExam(Document doc) {
        String id = doc.getObjectId("_id").toHexString();
        String title = doc.getString("title");
        String description = doc.getString("description");
        int duration = doc.getInteger("durationMinutes", 0);
        int passingScore = doc.getInteger("passingScore", 0);
        boolean published = doc.getBoolean("published", false);
        String createdBy = doc.getString("createdByUserId");
        
        Exam exam = new Exam(title, description, duration, passingScore, createdBy);
        exam.setId(id);
        exam.setPublished(published);
        
        // Load questions
        @SuppressWarnings("unchecked")
        List<Document> questionDocs = (List<Document>) doc.get("questions");
        if (questionDocs != null) {
            for (Document qDoc : questionDocs) {
                Question q = documentToQuestion(qDoc);
                exam.addQuestion(q);
            }
        }
        
        return exam;
    }

    private Document examToDocument(Exam exam) {
        List<Document> questionDocs = new ArrayList<>();
        for (Question q : exam.getQuestions()) {
            questionDocs.add(questionToDocument(q));
        }
        
        return new Document()
            .append("title", exam.getTitle())
            .append("description", exam.getDescription())
            .append("durationMinutes", exam.getDurationMinutes())
            .append("passingScore", exam.getPassingScore())
            .append("published", exam.isPublished())
            .append("createdByUserId", exam.getCreatedByUserId())
            .append("createdAt", exam.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("questions", questionDocs);
    }

    private Question documentToQuestion(Document doc) {
        ObjectId oid = doc.getObjectId("_id");
        String id = oid != null ? oid.toHexString() : UUID.randomUUID().toString();
        String text = doc.getString("text");
        String a = doc.getString("optionA");
        String b = doc.getString("optionB");
        String c = doc.getString("optionC");
        String d = doc.getString("optionD");
        char correct = doc.getString("correctAnswer").charAt(0);
        int marks = doc.getInteger("marks", 0);
        
        Question q = new Question(text, a, b, c, d, correct, marks);
        q.setId(id);
        return q;
    }

    private Document questionToDocument(Question q) {
        return new Document()
            .append("text", q.getText())
            .append("optionA", q.getOptionA())
            .append("optionB", q.getOptionB())
            .append("optionC", q.getOptionC())
            .append("optionD", q.getOptionD())
            .append("correctAnswer", String.valueOf(q.getCorrectAnswer()))
            .append("marks", q.getMarks());
    }

    private ExamAttempt documentToAttempt(Document doc) {
        String id = doc.getObjectId("_id").toHexString();
        String studentId = doc.getString("studentId");
        String examId = doc.getString("examId");
        String examTitle = doc.getString("examTitle");
        
        ExamAttempt attempt = new ExamAttempt(studentId, examId, examTitle);
        attempt.setId(id);
        
        // Load answers
        @SuppressWarnings("unchecked")
        Document answersDoc = (Document) doc.get("answers");
        if (answersDoc != null) {
            for (String key : answersDoc.keySet()) {
                String value = answersDoc.getString(key);
                if (value != null && !value.isEmpty()) {
                    attempt.recordAnswer(key, value.charAt(0));
                }
            }
        }
        
        // Load submission data
        String statusStr = doc.getString("status");
        if ("SUBMITTED".equals(statusStr)) {
            attempt.submit(
                doc.getInteger("score", 0),
                doc.getInteger("totalMarks", 0),
                doc.getBoolean("passed", false)
            );
        } else if ("TIMED_OUT".equals(statusStr)) {
            attempt.timeout(
                doc.getInteger("score", 0),
                doc.getInteger("totalMarks", 0),
                doc.getBoolean("passed", false)
            );
        }
        
        return attempt;
    }

    private Document attemptToDocument(ExamAttempt a) {
        Document answersDoc = new Document();
        for (Map.Entry<String, Character> entry : a.getAnswers().entrySet()) {
            answersDoc.append(entry.getKey(), String.valueOf(entry.getValue()));
        }
        
        Document doc = new Document()
            .append("studentId", a.getStudentId())
            .append("examId", a.getExamId())
            .append("examTitle", a.getExamTitle())
            .append("startedAt", a.getStartedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("status", a.getStatus().toString())
            .append("answers", answersDoc);
            
        if (a.getStatus() == ExamAttempt.Status.SUBMITTED || a.getStatus() == ExamAttempt.Status.TIMED_OUT) {
            doc.append("submittedAt", a.getSubmittedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
               .append("score", a.getScore())
               .append("totalMarks", a.getTotalMarks())
               .append("percentage", a.getPercentage())
               .append("passed", a.isPassed());
        }
        
        return doc;
    }

    // ── Seed Data ─────────────────────────────────────────────────────────────

    private void seedData() {
        System.out.println("Seeding data...");
        // Drop existing collections to reseed
        usersCollection.drop();
        examsCollection.drop();
        attemptsCollection.drop();
        
        // Insert users
        Document adminDoc = new Document()
            .append("name", "Admin User")
            .append("email", "admin@exam.com")
            .append("password", "admin123")
            .append("role", "ADMIN");
        usersCollection.insertOne(adminDoc);
        String adminId = adminDoc.getObjectId("_id").toHexString();
        
        Document pulkitDoc = new Document()
            .append("name", "Pulkit")
            .append("email", "pulkit@gmail.com")
            .append("password", "pulkit123")
            .append("role", "STUDENT");
        usersCollection.insertOne(pulkitDoc);
        String pulkitId = pulkitDoc.getObjectId("_id").toHexString();
        
        Document vaibhavDoc = new Document()
            .append("name", "Vaibhav")
            .append("email", "vaibhav@gmail.com")
            .append("password", "vaibhav123")
            .append("role", "STUDENT");
        usersCollection.insertOne(vaibhavDoc);
        String vaibhavId = vaibhavDoc.getObjectId("_id").toHexString();
        System.out.println("Users seeded: admin, pulkit, vaibhav");

        // ── Exam 1: Java Basics ───────────────────────────────────────────────
        List<Document> javaQuestions = List.of(
            new Document().append("text", "Which keyword is used to define a class in Java?")
                         .append("optionA", "class").append("optionB", "define").append("optionC", "struct").append("optionD", "object")
                         .append("correctAnswer", "A").append("marks", 1),
            new Document().append("text", "What is the default value of an int variable?")
                         .append("optionA", "null").append("optionB", "0").append("optionC", "undefined").append("optionD", "-1")
                         .append("correctAnswer", "B").append("marks", 1),
            new Document().append("text", "Which of these is NOT a Java primitive type?")
                         .append("optionA", "int").append("optionB", "boolean").append("optionC", "String").append("optionD", "char")
                         .append("correctAnswer", "C").append("marks", 1),
            new Document().append("text", "What does JVM stand for?")
                         .append("optionA", "Java Virtual Machine").append("optionB", "Java Variable Method")
                         .append("optionC", "Java Verified Module").append("optionD", "Java Visual Manager")
                         .append("correctAnswer", "A").append("marks", 2),
            new Document().append("text", "Which method is the entry point of a Java application?")
                         .append("optionA", "start()").append("optionB", "run()").append("optionC", "main()").append("optionD", "init()")
                         .append("correctAnswer", "C").append("marks", 2),
            new Document().append("text", "Which operator is used to create an object in Java?")
                         .append("optionA", "create").append("optionB", "make").append("optionC", "new").append("optionD", "object")
                         .append("correctAnswer", "C").append("marks", 1),
            new Document().append("text", "What is the size of an int in Java?")
                         .append("optionA", "8 bits").append("optionB", "16 bits").append("optionC", "32 bits").append("optionD", "64 bits")
                         .append("correctAnswer", "C").append("marks", 1),
            new Document().append("text", "Which collection allows duplicate elements?")
                         .append("optionA", "Set").append("optionB", "Map").append("optionC", "List").append("optionD", "TreeSet")
                         .append("correctAnswer", "C").append("marks", 2)
        );
        
        Document javaDoc = new Document()
            .append("title", "Java Programming Basics")
            .append("description", "Test your knowledge of core Java concepts.")
            .append("durationMinutes", 30)
            .append("passingScore", 60)
            .append("published", true)
            .append("createdByUserId", adminId)
            .append("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("questions", javaQuestions);
        examsCollection.insertOne(javaDoc);
        String javaId = javaDoc.getObjectId("_id").toHexString();

        // ── Exam 2: General Knowledge ─────────────────────────────────────────
        List<Document> gkQuestions = List.of(
            new Document().append("text", "What is the chemical symbol for water?")
                         .append("optionA", "WA").append("optionB", "H2O").append("optionC", "HO").append("optionD", "W2O")
                         .append("correctAnswer", "B").append("marks", 1),
            new Document().append("text", "Which planet is closest to the Sun?")
                         .append("optionA", "Venus").append("optionB", "Earth").append("optionC", "Mercury").append("optionD", "Mars")
                         .append("correctAnswer", "C").append("marks", 1),
            new Document().append("text", "In which year did World War II end?")
                         .append("optionA", "1943").append("optionB", "1944").append("optionC", "1945").append("optionD", "1946")
                         .append("correctAnswer", "C").append("marks", 1),
            new Document().append("text", "Who wrote 'Romeo and Juliet'?")
                         .append("optionA", "Charles Dickens").append("optionB", "William Shakespeare")
                         .append("optionC", "Jane Austen").append("optionD", "Mark Twain")
                         .append("correctAnswer", "B").append("marks", 1),
            new Document().append("text", "What is the capital of Japan?")
                         .append("optionA", "Beijing").append("optionB", "Seoul").append("optionC", "Bangkok").append("optionD", "Tokyo")
                         .append("correctAnswer", "D").append("marks", 1),
            new Document().append("text", "How many continents are there on Earth?")
                         .append("optionA", "5").append("optionB", "6").append("optionC", "7").append("optionD", "8")
                         .append("correctAnswer", "C").append("marks", 1)
        );
        
        Document gkDoc = new Document()
            .append("title", "General Knowledge Quiz")
            .append("description", "A fun quiz on science, history, and world facts.")
            .append("durationMinutes", 20)
            .append("passingScore", 50)
            .append("published", true)
            .append("createdByUserId", adminId)
            .append("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("questions", gkQuestions);
        examsCollection.insertOne(gkDoc);
        String gkId = gkDoc.getObjectId("_id").toHexString();

        // ── Exam 3: Math Quiz ─────────────────────────────────────────────────
        List<Document> mathQuestions = List.of(
            new Document().append("text", "What is the value of π (pi) approximately?")
                         .append("optionA", "2.718").append("optionB", "3.141").append("optionC", "1.414").append("optionD", "1.732")
                         .append("correctAnswer", "B").append("marks", 1),
            new Document().append("text", "What is 15% of 200?")
                         .append("optionA", "25").append("optionB", "30").append("optionC", "35").append("optionD", "40")
                         .append("correctAnswer", "B").append("marks", 1),
            new Document().append("text", "Solve: 2x + 5 = 15. What is x?")
                         .append("optionA", "3").append("optionB", "4").append("optionC", "5").append("optionD", "6")
                         .append("correctAnswer", "C").append("marks", 2),
            new Document().append("text", "What is the area of a circle with radius 7? (Use π ≈ 22/7)")
                         .append("optionA", "144").append("optionB", "154").append("optionC", "164").append("optionD", "174")
                         .append("correctAnswer", "B").append("marks", 2),
            new Document().append("text", "What is the square root of 144?")
                         .append("optionA", "11").append("optionB", "12").append("optionC", "13").append("optionD", "14")
                         .append("correctAnswer", "B").append("marks", 1)
        );
        
        Document mathDoc = new Document()
            .append("title", "Mathematics Fundamentals")
            .append("description", "Basic algebra, arithmetic, and geometry.")
            .append("durationMinutes", 25)
            .append("passingScore", 70)
            .append("published", true)
            .append("createdByUserId", adminId)
            .append("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("questions", mathQuestions);
        examsCollection.insertOne(mathDoc);

        // ── Exam 4: Draft ─────────────────────────────────────────────────────
        List<Document> draftQuestions = List.of(
            new Document().append("text", "What is a lambda expression?")
                         .append("optionA", "A loop").append("optionB", "An anonymous function")
                         .append("optionC", "A class").append("optionD", "A variable")
                         .append("correctAnswer", "B").append("marks", 2)
        );
        
        Document draftDoc = new Document()
            .append("title", "Advanced Java (Draft)")
            .append("description", "Not published yet.")
            .append("durationMinutes", 60)
            .append("passingScore", 75)
            .append("published", false)
            .append("createdByUserId", adminId)
            .append("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("questions", draftQuestions);
        examsCollection.insertOne(draftDoc);

        // ── Sample attempts ───────────────────────────────────────────────────
        // For attempts, we need to get the question IDs from the documents
        // For simplicity, let's assume the questions are inserted in order and we can reference them by index
        // But since MongoDB generates IDs, we'll use placeholder IDs for now
        Document a1Doc = new Document()
            .append("studentId", pulkitId)
            .append("examId", javaId)
            .append("examTitle", "Java Programming Basics")
            .append("startedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("status", "SUBMITTED")
            .append("answers", new Document()) // Empty for now
            .append("submittedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("score", 9)
            .append("totalMarks", 12)
            .append("percentage", 75.0)
            .append("passed", true);
        attemptsCollection.insertOne(a1Doc);

        Document a2Doc = new Document()
            .append("studentId", vaibhavId)
            .append("examId", gkId)
            .append("examTitle", "General Knowledge Quiz")
            .append("startedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("status", "SUBMITTED")
            .append("answers", new Document())
            .append("submittedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .append("score", 6)
            .append("totalMarks", 6)
            .append("percentage", 100.0)
            .append("passed", true);
        attemptsCollection.insertOne(a2Doc);
    }
}
