package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * JavaFX Student Dashboard — Available Exams + My Results tabs.
 */
public class StudentDashboardScreen {

    private final Stage stage;
    private final User student = DataStore.get().getCurrentUser();
    private final BorderPane root;

    public StudentDashboardScreen(Stage stage) {
        this.stage = stage;
        this.root = build();
    }

    public BorderPane getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setTop(buildNavBar());
        bp.setCenter(buildTabs());
        return bp;
    }

    private HBox buildNavBar() {
        HBox bar = UITheme.navBar();
        Label brand = new Label("🎓 ExamPortal");
        brand.setFont(Font.font("SansSerif", FontWeight.BOLD, 17));
        brand.setTextFill(Color.web(UITheme.ACCENT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = UITheme.muted("👤 " + student.getName());
        Button logoutBtn = UITheme.ghostButton("Logout");
        logoutBtn.setOnAction(e -> {
            DataStore.get().logout();
            LoginScreen login = new LoginScreen(stage);
            Scene scene = new Scene(login.getRoot(), 480, 600);
            LoginScreen.applyCSS(scene);
            stage.setTitle("ExamPortal — Login");
            stage.setResizable(false);
            stage.setScene(scene);
        });

        bar.getChildren().addAll(brand, spacer, userLabel, logoutBtn);
        HBox.setMargin(logoutBtn, new Insets(0, 0, 0, 10));
        return bar;
    }

    private TabPane buildTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab examTab = new Tab("  📚  Available Exams  ", buildExamListPanel());
        Tab resultTab = new Tab("  📊  My Results  ", buildResultsPanel());
        tabs.getTabs().addAll(examTab, resultTab);
        return tabs;
    }

    // ── Available Exams Tab ───────────────────────────────────────────────────

    private ScrollPane buildExamListPanel() {
        VBox panel = new VBox(12);
        panel.setStyle("-fx-background-color: #0F1923;");
        panel.setPadding(new Insets(24, 32, 24, 32));

        List<Exam> exams = DataStore.get().getPublishedExams();

        if (exams.isEmpty()) {
            Label empty = UITheme.muted("No exams available right now.");
            panel.setAlignment(Pos.CENTER);
            panel.getChildren().add(empty);
        } else {
            Label heading = UITheme.heading("Available Exams");
            Label sub = UITheme.muted(exams.size() + " exam(s) ready for you");
            panel.getChildren().addAll(heading, sub);
            for (Exam exam : exams) {
                panel.getChildren().add(buildExamCard(exam));
            }
        }

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private HBox buildExamCard(Exam exam) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """);
        card.setPadding(new Insets(18, 20, 18, 20));

        // Left info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLabel = new Label(exam.getTitle());
        titleLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        titleLabel.setTextFill(Color.web(UITheme.TEXT_PRIMARY));

        Label desc = UITheme.muted(exam.getDescription() != null ? exam.getDescription() : "");

        HBox meta = new HBox(10);
        meta.setAlignment(Pos.CENTER_LEFT);
        meta.getChildren().addAll(
            UITheme.chip("⏱ " + exam.getDurationMinutes() + " min"),
            UITheme.chip("❓ " + exam.getQuestions().size() + " questions"),
            UITheme.chip("✅ Pass: " + exam.getPassingScore() + "%")
        );

        info.getChildren().addAll(titleLabel, desc, meta);

        // Start button
        Button startBtn = UITheme.primaryButton("Start Exam →");
        startBtn.setOnAction(e -> startExam(exam));

        card.getChildren().addAll(info, startBtn);
        return card;
    }

    // ── My Results Tab ────────────────────────────────────────────────────────

    private ScrollPane buildResultsPanel() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: #0F1923;");
        panel.setPadding(new Insets(24, 32, 24, 32));

        List<ExamAttempt> attempts = DataStore.get()
            .getAttemptsForStudent(student.getId()).stream()
            .filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED || a.getStatus() == ExamAttempt.Status.TIMED_OUT)
            .toList();

        Label heading = UITheme.heading("My Exam Results");
        Label sub = UITheme.muted(attempts.size() + " completed exam(s)");
        panel.getChildren().addAll(heading, sub);

        if (attempts.isEmpty()) {
            Label empty = UITheme.muted("You haven't completed any exams yet.");
            VBox.setMargin(empty, new Insets(30, 0, 0, 0));
            panel.getChildren().add(empty);
        } else {
            for (ExamAttempt attempt : attempts) {
                panel.getChildren().add(buildResultCard(attempt));
            }
        }

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private HBox buildResultCard(ExamAttempt attempt) {
        boolean passed = attempt.isPassed();
        String borderColor = passed ? "rgba(76,175,80,0.5)" : "rgba(255,95,95,0.5)";

        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(String.format("""
            -fx-background-color: #162030;
            -fx-border-color: %s;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """, borderColor));
        card.setPadding(new Insets(16, 20, 16, 20));

        VBox left = new VBox(4);
        HBox.setHgrow(left, Priority.ALWAYS);

        Label name = new Label(attempt.getExamTitle());
        name.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        name.setTextFill(Color.web(UITheme.TEXT_PRIMARY));

        String dateStr = attempt.getSubmittedAt() != null
            ? attempt.getSubmittedAt().toLocalDate().toString() : "—";
        Label meta = UITheme.muted(String.format("Score: %d / %d  ·  Submitted: %s",
            attempt.getScore(), attempt.getTotalMarks(), dateStr));

        left.getChildren().addAll(name, meta);

        // Score pill
        String pct = String.format("%.1f%%", attempt.getPercentage());
        String verdict = passed ? "  PASS" : "  FAIL";
        Label pill = new Label(pct + verdict);
        pill.setFont(Font.font("SansSerif", FontWeight.BOLD, 15));
        pill.setTextFill(passed ? Color.web(UITheme.ACCENT_GREEN) : Color.web(UITheme.ACCENT_DANGER));
        pill.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-background-radius: 20;
            -fx-padding: 5 16 5 16;
            """, passed ? "rgba(76,175,80,0.15)" : "rgba(255,95,95,0.15)"));

        card.getChildren().addAll(left, pill);
        return card;
    }

    // ── Start Exam ────────────────────────────────────────────────────────────

    private void startExam(Exam exam) {
        if (DataStore.get().hasActiveAttempt(student.getId(), exam.getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                "You already have an attempt in progress for this exam.",
                ButtonType.OK);
            alert.setHeaderText("Active Attempt");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Start Exam");
        confirm.setContentText(String.format(
            "%s\n\nDuration: %d minutes\nQuestions: %d\nPassing Score: %d%%\n\nAre you ready?",
            exam.getTitle(), exam.getDurationMinutes(),
            exam.getQuestions().size(), exam.getPassingScore()));

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                ExamAttempt attempt = new ExamAttempt(student.getId(), exam.getId(), exam.getTitle());
                DataStore.get().addAttempt(attempt);

                ExamScreen examScreen = new ExamScreen(exam, attempt, stage);
                Scene scene = new Scene(examScreen.getRoot(), 1000, 680);
                LoginScreen.applyCSS(scene);
                stage.setTitle(exam.getTitle());
                stage.setScene(scene);
            }
        });
    }
}
