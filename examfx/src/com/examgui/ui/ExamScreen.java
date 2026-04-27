package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/**
 * JavaFX Exam Screen — live exam with countdown timer and sidebar navigator.
 */
public class ExamScreen {

    private final Exam exam;
    private final ExamAttempt attempt;
    private final Stage stage;
    private final List<Question> questions;

    private int currentIndex = 0;
    private final Map<Integer, Character> answers = new HashMap<>();
    private Timeline timer;
    private int secondsLeft;

    // Dynamic UI refs
    private Label timerLabel;
    private Label questionCounter;
    private Label questionText;
    private ToggleGroup optionGroup;
    private RadioButton[] optionBtns;
    private FlowPane navPanel;
    private Button prevBtn, nextBtn;

    private final BorderPane root;

    public ExamScreen(Exam exam, ExamAttempt attempt, Stage stage) {
        this.exam = exam;
        this.attempt = attempt;
        this.stage = stage;
        this.questions = exam.getQuestions();
        this.secondsLeft = exam.getDurationMinutes() * 60;

        this.root = build();
        startTimer();
        loadQuestion(0);

        // Intercept close
        stage.setOnCloseRequest(e -> {
            e.consume();
            confirmExit();
        });
    }

    public BorderPane getRoot() { return root; }

    private BorderPane build() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setTop(buildHeader());
        bp.setCenter(buildMain());
        bp.setBottom(buildBottomBar());
        return bp;
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private HBox buildHeader() {
        HBox bar = UITheme.navBar();

        Label examTitle = new Label("📝  " + exam.getTitle());
        examTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        examTitle.setTextFill(Color.web(UITheme.TEXT_PRIMARY));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        questionCounter = UITheme.muted("Question 1 of " + questions.size());

        timerLabel = new Label("⏱  --:--");
        timerLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        timerLabel.setTextFill(Color.web(UITheme.ACCENT));

        bar.getChildren().addAll(examTitle, spacer, questionCounter, timerLabel);
        HBox.setMargin(timerLabel, new Insets(0, 0, 0, 20));
        return bar;
    }

    // ── Main ──────────────────────────────────────────────────────────────────

    private HBox buildMain() {
        HBox main = new HBox();
        main.setStyle("-fx-background-color: #0F1923;");

        ScrollPane questionArea = buildQuestionArea();
        HBox.setHgrow(questionArea, Priority.ALWAYS);

        VBox sidebar = buildSidebar();

        main.getChildren().addAll(questionArea, sidebar);
        return main;
    }

    private ScrollPane buildQuestionArea() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #0F1923;");
        panel.setPadding(new Insets(30, 40, 20, 40));

        // Question text
        questionText = new Label("Loading...");
        questionText.setFont(Font.font("SansSerif", 16));
        questionText.setTextFill(Color.web(UITheme.TEXT_PRIMARY));
        questionText.setWrapText(true);
        questionText.setMaxWidth(600);

        // Options
        optionGroup = new ToggleGroup();
        optionBtns = new RadioButton[4];
        char[] labels = {'A', 'B', 'C', 'D'};

        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(24, 0, 0, 0));

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            final char letter = labels[i];
            RadioButton rb = createOptionButton(letter, "");
            rb.setToggleGroup(optionGroup);
            rb.setOnAction(e -> {
                answers.put(currentIndex, letter);
                updateNavPanel();
            });
            optionBtns[i] = rb;
            optionsBox.getChildren().add(rb);
        }

        panel.getChildren().addAll(questionText, optionsBox);

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private RadioButton createOptionButton(char label, String text) {
        RadioButton rb = new RadioButton(label + ".  " + text);
        rb.setFont(Font.font("SansSerif", 14));
        rb.setTextFill(Color.web(UITheme.TEXT_PRIMARY));
        rb.setMaxWidth(600);
        rb.setStyle("""
            -fx-background-color: #1C2B3A;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 14 18 14 18;
            -fx-cursor: hand;
            """);
        rb.selectedProperty().addListener((obs, old, selected) -> {
            if (selected) {
                rb.setStyle(rb.getStyle()
                    .replace("#1C2B3A", "rgba(0,201,167,0.15)")
                    + "-fx-border-color: rgba(0,201,167,0.5); -fx-border-width: 1;");
            } else {
                rb.setStyle("""
                    -fx-background-color: #1C2B3A;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-padding: 14 18 14 18;
                    -fx-cursor: hand;
                    """);
            }
        });
        return rb;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: transparent transparent transparent #243447;
            -fx-border-width: 0 0 0 1;
            """);
        sidebar.setPadding(new Insets(20, 16, 20, 16));
        sidebar.setPrefWidth(200);
        sidebar.setMinWidth(200);

        Label navTitle = UITheme.heading("Questions");

        // Legend
        VBox legend = new VBox(4);
        legend.getChildren().addAll(
            legendItem(UITheme.ACCENT_GREEN, "Answered"),
            legendItem(UITheme.TEXT_MUTED, "Not answered"),
            legendItem(UITheme.ACCENT, "Current")
        );

        navPanel = new FlowPane(6, 6);
        navPanel.setPrefWrapLength(168);

        sidebar.getChildren().addAll(navTitle, UITheme.separator(), legend,
            UITheme.separator(), navPanel);
        return sidebar;
    }

    private Label legendItem(String color, String text) {
        Label l = new Label("●  " + text);
        l.setFont(Font.font("SansSerif", 11));
        l.setTextFill(Color.web(color));
        return l;
    }

    private HBox buildBottomBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447 transparent transparent transparent;
            -fx-border-width: 1 0 0 0;
            """);
        bar.setPadding(new Insets(12, 24, 12, 24));

        prevBtn = UITheme.ghostButton("← Previous");
        prevBtn.setOnAction(e -> navigate(-1));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nextBtn = UITheme.primaryButton("Next →");
        nextBtn.setOnAction(e -> navigate(1));

        Button submitBtn = UITheme.warningButton("Submit Exam");
        submitBtn.setOnAction(e -> confirmSubmit());

        bar.getChildren().addAll(prevBtn, spacer, nextBtn, submitBtn);
        return bar;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        currentIndex = index;
        Question q = questions.get(index);

        questionText.setText("Q" + (index + 1) + ". " + q.getText());
        questionCounter.setText("Question " + (index + 1) + " of " + questions.size());

        char[] labels = {'A', 'B', 'C', 'D'};
        String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setText(labels[i] + ".  " + opts[i]);
            optionBtns[i].setSelected(false);
        }

        // Restore previous answer
        Character prev = answers.get(currentIndex);
        if (prev != null) {
            int btnIdx = prev - 'A';
            if (btnIdx >= 0 && btnIdx < 4) optionBtns[btnIdx].setSelected(true);
        }

        prevBtn.setDisable(index == 0);
        nextBtn.setDisable(index == questions.size() - 1);
        updateNavPanel();
    }

    private void navigate(int dir) { loadQuestion(currentIndex + dir); }

    private void updateNavPanel() {
        if (navPanel == null) return;
        navPanel.getChildren().clear();
        for (int i = 0; i < questions.size(); i++) {
            final int qi = i;
            boolean answered  = answers.containsKey(i);
            boolean isCurrent = (i == currentIndex);

            Button nb = new Button(String.valueOf(i + 1));
            nb.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
            nb.setPrefSize(34, 34);
            nb.setMinSize(34, 34);
            nb.setMaxSize(34, 34);

            String bg = isCurrent ? "#00C9A7" :
                        answered  ? "rgba(76,175,80,0.5)" : "#1C2B3A";
            String fg = isCurrent ? "#0F1923" :
                        answered  ? "#4CAF50" : "#7A8FA6";
            nb.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 6;
                -fx-text-fill: %s;
                -fx-cursor: hand;
                """, bg, fg));

            nb.setOnAction(e -> loadQuestion(qi));
            navPanel.getChildren().add(nb);
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            int min = secondsLeft / 60, sec = secondsLeft % 60;
            timerLabel.setText(String.format("⏱  %02d:%02d", min, sec));

            if (secondsLeft <= 300) timerLabel.setTextFill(Color.web(UITheme.ACCENT_WARN));
            if (secondsLeft <= 60)  timerLabel.setTextFill(Color.web(UITheme.ACCENT_DANGER));

            if (secondsLeft <= 0) {
                timer.stop();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Time's up! Your exam is being submitted.", ButtonType.OK);
                    alert.setHeaderText("Time Over");
                    alert.showAndWait();
                    doSubmit(true);
                });
            }
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void confirmSubmit() {
        int answered   = answers.size();
        int total      = questions.size();
        int unanswered = total - answered;

        String msg = String.format("Answered: %d / %d questions\n%s\nOnce submitted, you cannot change your answers.",
            answered, total,
            unanswered > 0 ? "Unanswered: " + unanswered + " (will be marked 0)\n" : "");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Submit Exam?");
        confirm.showAndWait().ifPresent(r -> { if (r == ButtonType.YES) doSubmit(); });
    }

    private void doSubmit() {
        doSubmit(false);
    }

    private void doSubmit(boolean isTimeout) {
        if (timer != null) timer.stop();

        int score = 0, totalMarks = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            totalMarks += q.getMarks();
            Character selected = answers.get(i);
            if (selected != null && selected == q.getCorrectAnswer()) {
                score += q.getMarks();
            }
            if (selected != null) attempt.recordAnswer(q.getId(), selected);
        }

        double pct = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        boolean passed = pct >= exam.getPassingScore();
        
        if (isTimeout) {
            attempt.timeout(score, totalMarks, passed);
        } else {
            attempt.submit(score, totalMarks, passed);
        }
        
        DataStore.get().updateAttempt(attempt);

        // Reset close handler
        stage.setOnCloseRequest(null);

        ResultScreen result = new ResultScreen(exam, attempt, questions, answers, stage);
        Scene scene = new Scene(result.getRoot(), 780, 700);
        LoginScreen.applyCSS(scene);
        stage.setTitle("Exam Result — " + exam.getTitle());
        stage.setScene(scene);
    }

    private void confirmExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to exit? Your progress will be lost!",
            ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Exit Exam");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                if (timer != null) timer.stop();
                DataStore.get().getAttemptsForStudent(attempt.getStudentId())
                    .removeIf(a -> a.getId().equals(attempt.getId()));
                stage.setOnCloseRequest(null);
                StudentDashboardScreen sd = new StudentDashboardScreen(stage);
                Scene scene = new Scene(sd.getRoot(), 900, 640);
                LoginScreen.applyCSS(scene);
                stage.setTitle("ExamPortal — " + DataStore.get().getCurrentUser().getName());
                stage.setScene(scene);
            }
        });
    }
}
