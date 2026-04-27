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

import java.util.*;

/**
 * JavaFX Result Screen — score summary + full answer review.
 */
public class ResultScreen {

    private final Exam exam;
    private final ExamAttempt attempt;
    private final List<Question> questions;
    private final Map<Integer, Character> answers; // qIndex → selected
    private final Stage stage;

    private final BorderPane root;

    public ResultScreen(Exam exam, ExamAttempt attempt, List<Question> questions,
                        Map<Integer, Character> answers, Stage stage) {
        this.exam = exam;
        this.attempt = attempt;
        this.questions = questions;
        this.answers = answers;
        this.stage = stage;
        this.root = build();
    }

    public BorderPane getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setTop(buildHeader());
        bp.setCenter(buildReview());
        bp.setBottom(buildFooter());
        return bp;
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private VBox buildHeader() {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: transparent transparent #243447 transparent;
            -fx-border-width: 0 0 1 0;
            """);
        panel.setPadding(new Insets(28, 40, 28, 40));

        boolean passed = attempt.isPassed();
        String verdictColor = passed ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;

        Label icon = new Label(passed ? "🎉" : "📝");
        icon.setFont(Font.font("SansSerif", 40));

        Label verdict = new Label(passed ? "Congratulations! You Passed!" : "Keep Practicing!");
        verdict.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));
        verdict.setTextFill(Color.web(verdictColor));

        Label examName = UITheme.muted(exam.getTitle());

        // Stats row
        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER);
        stats.getChildren().addAll(
            statBox("Score", attempt.getScore() + " / " + attempt.getTotalMarks(), UITheme.ACCENT),
            statBox("Percentage", String.format("%.1f%%", attempt.getPercentage()), verdictColor),
            statBox("Passing", exam.getPassingScore() + "%", UITheme.TEXT_MUTED),
            statBox("Questions", String.valueOf(questions.size()), UITheme.TEXT_MUTED)
        );

        panel.getChildren().addAll(icon, verdict, examName, stats);
        return panel;
    }

    private VBox statBox(String label, String value, String valueColor) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setStyle("""
            -fx-background-color: #1C2B3A;
            -fx-border-color: #243447;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);
        box.setPadding(new Insets(12, 20, 12, 20));

        Label val = new Label(value);
        val.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        val.setTextFill(Color.web(valueColor));

        Label lbl = UITheme.muted(label);

        box.getChildren().addAll(val, lbl);
        return box;
    }

    // ── Answer Review ─────────────────────────────────────────────────────────

    private ScrollPane buildReview() {
        VBox panel = new VBox(10);
        panel.setStyle("-fx-background-color: #0F1923;");
        panel.setPadding(new Insets(20, 32, 20, 32));

        Label heading = UITheme.heading("Answer Review");
        Label sub = UITheme.muted("Review each question and the correct answers");
        panel.getChildren().addAll(heading, sub);

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Character selected = answers.get(i);
            boolean correct = selected != null && selected == q.getCorrectAnswer();
            panel.getChildren().add(buildAnswerCard(i + 1, q, selected, correct));
        }

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private VBox buildAnswerCard(int num, Question q, Character selected, boolean correct) {
        String borderColor = selected == null ? UITheme.TEXT_MUTED :
                             correct ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;

        VBox card = new VBox(8);
        card.setStyle(String.format("""
            -fx-background-color: #162030;
            -fx-border-color: %s;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """, borderColor));
        card.setPadding(new Insets(14, 18, 14, 18));

        // Top row
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label numLabel = new Label("Q" + num);
        numLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        numLabel.setTextFill(Color.web(UITheme.TEXT_MUTED));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String marksText = "+" + (correct ? q.getMarks() : 0) + " / " + q.getMarks() + " marks";
        Label marksLabel = new Label(marksText);
        marksLabel.setFont(Font.font("SansSerif", 12));
        marksLabel.setTextFill(correct ? Color.web(UITheme.ACCENT_GREEN) : Color.web(UITheme.TEXT_MUTED));

        String resultIcon = selected == null ? "⬜ Skipped" : correct ? "✅ Correct" : "❌ Incorrect";
        String resultColor = selected == null ? UITheme.TEXT_MUTED : correct ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;
        Label resultLabel = new Label(resultIcon);
        resultLabel.setFont(Font.font("SansSerif", 12));
        resultLabel.setTextFill(Color.web(resultColor));

        topRow.getChildren().addAll(numLabel, spacer, marksLabel, resultLabel);
        HBox.setMargin(resultLabel, new Insets(0, 0, 0, 12));

        // Question text
        Label qText = new Label(q.getText());
        qText.setFont(Font.font("SansSerif", 14));
        qText.setTextFill(Color.web(UITheme.TEXT_PRIMARY));
        qText.setWrapText(true);

        // Answer row
        HBox ansRow = new HBox(14);
        ansRow.setAlignment(Pos.CENTER_LEFT);

        if (selected != null && !correct) {
            Label yours = new Label("Your answer: " + selected + ". " + q.getOption(selected));
            yours.setFont(Font.font("SansSerif", 12));
            yours.setTextFill(Color.web(UITheme.ACCENT_DANGER));
            ansRow.getChildren().add(yours);
        }

        Label correctLabel = new Label("✓ Correct: " + q.getCorrectAnswer() + ". " + q.getOption(q.getCorrectAnswer()));
        correctLabel.setFont(Font.font("SansSerif", 12));
        correctLabel.setTextFill(Color.web(UITheme.ACCENT_GREEN));
        ansRow.getChildren().add(correctLabel);

        card.getChildren().addAll(topRow, qText, ansRow);
        return card;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private HBox buildFooter() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447 transparent transparent transparent;
            -fx-border-width: 1 0 0 0;
            """);
        bar.setPadding(new Insets(14, 0, 14, 0));

        Button backBtn = UITheme.primaryButton("Back to Dashboard");
        backBtn.setOnAction(e -> {
            stage.setOnCloseRequest(null);
            StudentDashboardScreen sd = new StudentDashboardScreen(stage);
            Scene scene = new Scene(sd.getRoot(), 900, 640);
            LoginScreen.applyCSS(scene);
            stage.setTitle("ExamPortal — " + DataStore.get().getCurrentUser().getName());
            stage.setResizable(true);
            stage.setScene(scene);
        });

        bar.getChildren().add(backBtn);
        return bar;
    }
}
