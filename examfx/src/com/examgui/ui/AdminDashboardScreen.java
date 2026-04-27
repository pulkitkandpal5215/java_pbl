package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/**
 * JavaFX Admin Dashboard — Manage Exams | Students | Reports.
 */
public class AdminDashboardScreen {

    private final Stage stage;
    private final User admin = DataStore.get().getCurrentUser();
    private final BorderPane root;

    public AdminDashboardScreen(Stage stage) {
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

    // ── Nav Bar ───────────────────────────────────────────────────────────────

    private HBox buildNavBar() {
        HBox bar = UITheme.navBar();
        Label brand = new Label("🎓 ExamPortal  —  Admin");
        brand.setFont(Font.font("SansSerif", FontWeight.BOLD, 17));
        brand.setTextFill(Color.web(UITheme.ACCENT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = UITheme.muted("🛡  " + admin.getName());
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

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private TabPane buildTabs() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            new Tab("  📋  Manage Exams  ", buildExamTab()),
            new Tab("  👥  Students  ",     buildStudentsTab()),
            new Tab("  📊  Reports  ",      buildReportsTab())
        );
        return tabs;
    }

    // ── Tab 1: Manage Exams ───────────────────────────────────────────────────

    private BorderPane buildExamTab() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setPadding(new Insets(20, 24, 20, 24));

        // Header row
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 16, 0));
        Label title = UITheme.heading("All Exams");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button createBtn = UITheme.primaryButton("+ Create Exam");
        createBtn.setOnAction(e -> openCreateExamDialog());
        header.getChildren().addAll(title, spacer, createBtn);

        // Table
        TableView<Exam> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-color: #162030;");

        TableColumn<Exam, String> idCol = col("ID", 50);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));

        TableColumn<Exam, String> titleCol = col("Title", 250);
        titleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));

        TableColumn<Exam, String> qCol = col("Questions", 90);
        qCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getQuestions().size())));

        TableColumn<Exam, String> durCol = col("Duration", 90);
        durCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDurationMinutes() + " min"));

        TableColumn<Exam, String> passCol = col("Pass %", 70);
        passCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPassingScore() + "%"));

        TableColumn<Exam, String> statusCol = col("Status", 90);
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().isPublished() ? "Published" : "Draft"));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setTextFill("Published".equals(item)
                    ? Color.web(UITheme.ACCENT_GREEN)
                    : Color.web(UITheme.ACCENT_WARN));
                setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
            }
        });

        TableColumn<Exam, Void> actCol = new TableColumn<>("Actions");
        actCol.setMinWidth(240);
        actCol.setCellFactory(col -> new TableCell<>() {
            private final Button qBtn   = smallBtn("Questions", UITheme.ACCENT);
            private final Button pubBtn = smallBtn("Publish",   UITheme.ACCENT_WARN);
            private final Button delBtn = smallBtn("Delete",    UITheme.ACCENT_DANGER);
            {
                qBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    openQuestionsDialog(exam, table);
                });
                pubBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    exam.setPublished(!exam.isPublished());
                    DataStore.get().updateExam(exam);
                    pubBtn.setText(exam.isPublished() ? "Unpublish" : "Publish");
                    getTableView().refresh();
                });
                delBtn.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete exam \"" + exam.getTitle() + "\"?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("Confirm Delete");
                    confirm.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.YES) {
                            DataStore.get().removeExam(exam);
                            refreshExamTable(table);
                        }
                    });
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Exam exam = getTableView().getItems().get(getIndex());
                pubBtn.setText(exam.isPublished() ? "Unpublish" : "Publish");
                HBox box = new HBox(6, qBtn, pubBtn, delBtn);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(4, 0, 4, 0));
                setGraphic(box);
            }
        });

        table.getColumns().addAll(idCol, titleCol, qCol, durCol, passCol, statusCol, actCol);
        refreshExamTable(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        bp.setTop(header);
        bp.setCenter(table);
        return bp;
    }

    private void refreshExamTable(TableView<Exam> table) {
        table.setItems(FXCollections.observableArrayList(DataStore.get().getAllExams()));
    }

    // ── Tab 2: Students ───────────────────────────────────────────────────────

    private BorderPane buildStudentsTab() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setPadding(new Insets(20, 24, 20, 24));

        Label heading = UITheme.heading("Registered Students");
        heading.setPadding(new Insets(0, 0, 16, 0));

        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, String> idCol = col("ID", 50);
        idCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));

        TableColumn<User, String> nameCol = col("Name", 180);
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));

        TableColumn<User, String> emailCol = col("Email", 220);
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<User, String> attemptsCol = col("Exams Taken", 100);
        attemptsCol.setCellValueFactory(d -> {
            int count = DataStore.get().getAttemptsForStudent(d.getValue().getId())
                .stream().filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED || a.getStatus() == ExamAttempt.Status.TIMED_OUT).toList().size();
            return new SimpleStringProperty(String.valueOf(count));
        });

        TableColumn<User, String> avgCol = col("Avg Score", 100);
        avgCol.setCellValueFactory(d -> {
            List<ExamAttempt> att = DataStore.get().getAttemptsForStudent(d.getValue().getId())
                .stream().filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED || a.getStatus() == ExamAttempt.Status.TIMED_OUT).toList();
            if (att.isEmpty()) return new SimpleStringProperty("—");
            double avg = att.stream().mapToDouble(ExamAttempt::getPercentage).average().orElse(0);
            return new SimpleStringProperty(String.format("%.1f%%", avg));
        });

        table.getColumns().addAll(idCol, nameCol, emailCol, attemptsCol, avgCol);
        table.setItems(FXCollections.observableArrayList(DataStore.get().getAllStudents()));

        VBox top = new VBox(0, heading);
        bp.setTop(top);
        bp.setCenter(table);
        return bp;
    }

    // ── Tab 3: Reports ────────────────────────────────────────────────────────

    private BorderPane buildReportsTab() {
        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setPadding(new Insets(20, 24, 20, 24));

        List<ExamAttempt> all = DataStore.get().getAllAttempts().stream()
            .filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED || a.getStatus() == ExamAttempt.Status.TIMED_OUT).toList();
        long passed = all.stream().filter(ExamAttempt::isPassed).count();
        long failed = all.size() - passed;
        double avgScore = all.isEmpty() ? 0 :
            all.stream().mapToDouble(ExamAttempt::getPercentage).average().orElse(0);

        // Summary cards
        HBox cards = new HBox(16);
        cards.setPadding(new Insets(0, 0, 20, 0));
        cards.getChildren().addAll(
            summaryCard("📋", "Total Attempts", String.valueOf(all.size()), UITheme.ACCENT),
            summaryCard("✅", "Passed",          String.valueOf(passed),    UITheme.ACCENT_GREEN),
            summaryCard("❌", "Failed",           String.valueOf(failed),    UITheme.ACCENT_DANGER),
            summaryCard("📊", "Avg Score",        String.format("%.1f%%", avgScore), UITheme.ACCENT_WARN)
        );

        // Attempts table
        Label tableTitle = UITheme.heading("All Exam Attempts");
        tableTitle.setPadding(new Insets(0, 0, 12, 0));

        TableView<ExamAttempt> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ExamAttempt, String> studentCol = col("Student", 150);
        studentCol.setCellValueFactory(d -> {
            Optional<User> u = DataStore.get().getUserById(d.getValue().getStudentId());
            return new SimpleStringProperty(u.map(User::getName).orElse("Unknown"));
        });

        TableColumn<ExamAttempt, String> examCol = col("Exam", 200);
        examCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getExamTitle()));

        TableColumn<ExamAttempt, String> scoreCol = col("Score", 100);
        scoreCol.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getScore() + " / " + d.getValue().getTotalMarks()));

        TableColumn<ExamAttempt, String> pctCol = col("Percentage", 100);
        pctCol.setCellValueFactory(d -> new SimpleStringProperty(
            String.format("%.1f%%", d.getValue().getPercentage())));

        TableColumn<ExamAttempt, String> resultCol = col("Result", 80);
        resultCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isPassed() ? "PASS" : "FAIL"));
        resultCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill("PASS".equals(item)
                    ? Color.web(UITheme.ACCENT_GREEN)
                    : Color.web(UITheme.ACCENT_DANGER));
                setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(studentCol, examCol, scoreCol, pctCol, resultCol);
        table.setItems(FXCollections.observableArrayList(all));

        VBox center = new VBox(0, tableTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        bp.setTop(cards);
        bp.setCenter(center);
        return bp;
    }

    private VBox summaryCard(String icon, String label, String value, String valueColor) {
        VBox card = new VBox(4);
        card.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """);
        card.setPadding(new Insets(18, 20, 18, 20));

        Label ico = UITheme.muted(icon + "  " + label);
        Label val = new Label(value);
        val.setFont(Font.font("SansSerif", FontWeight.BOLD, 30));
        val.setTextFill(Color.web(valueColor));

        card.getChildren().addAll(ico, val);
        return card;
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private void openCreateExamDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Create New Exam");
        dialog.initOwner(stage);

        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #162030;");
        panel.setPadding(new Insets(24, 28, 24, 28));

        Label heading = UITheme.heading("Create New Exam");
        VBox.setMargin(heading, new Insets(0, 0, 16, 0));

        TextField titleF = UITheme.textField("Exam title");
        TextField descF  = UITheme.textField("Description");
        TextField durF   = UITheme.textField("Duration (minutes)");
        TextField passF  = UITheme.textField("Passing score (%)");
        for (TextField tf : new TextField[]{titleF, descF, durF, passF}) {
            tf.setMaxWidth(Double.MAX_VALUE);
        }

        Label errLbl = UITheme.errorLabel();

        Button createBtn = UITheme.primaryButton("Create Exam");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setOnAction(e -> {
            try {
                String t = titleF.getText().trim();
                if (t.isEmpty()) { errLbl.setText("Title is required."); return; }
                int dur  = Integer.parseInt(durF.getText().trim());
                int pass = Integer.parseInt(passF.getText().trim());
                Exam exam = new Exam(t, descF.getText().trim(), dur, pass, admin.getId());
                DataStore.get().addExam(exam);
                dialog.close();
                Alert info = new Alert(Alert.AlertType.INFORMATION,
                    "Exam created! Now go to Manage Exams to add questions.", ButtonType.OK);
                info.setHeaderText("Created");
                info.showAndWait();
                refresh();
            } catch (NumberFormatException ex) {
                errLbl.setText("Duration and pass score must be numbers.");
            }
        });

        panel.getChildren().addAll(
            heading,
            UITheme.fieldLabel("Title"),               titleF,
            UITheme.fieldLabel("Description"),         descF,
            UITheme.fieldLabel("Duration (minutes)"),  durF,
            UITheme.fieldLabel("Passing Score (%)"),   passF,
            errLbl, createBtn
        );
        for (int i = 1; i < panel.getChildren().size(); i++) {
            VBox.setMargin(panel.getChildren().get(i), new Insets(4, 0, 0, 0));
        }
        VBox.setMargin(createBtn, new Insets(14, 0, 0, 0));

        Scene scene = new Scene(panel, 440, 380);
        LoginScreen.applyCSS(scene);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openQuestionsDialog(Exam exam, TableView<Exam> examTable) {
        Stage dialog = new Stage();
        dialog.setTitle("Questions — " + exam.getTitle());
        dialog.initOwner(stage);

        BorderPane bp = new BorderPane();
        bp.setStyle("-fx-background-color: #0F1923;");
        bp.setPadding(new Insets(20, 24, 20, 24));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 14, 0));
        Label title = UITheme.heading("Questions for: " + exam.getTitle());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button addBtn = UITheme.primaryButton("+ Add Question");
        addBtn.setOnAction(e -> openAddQuestionDialog(exam, dialog, examTable));
        header.getChildren().addAll(title, spacer, addBtn);

        // Table
        TableView<Question> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Question, String> numCol = col("#", 40);
        numCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        TableColumn<Question, String> qCol = col("Question", 280);
        qCol.setCellValueFactory(d -> new SimpleStringProperty(truncate(d.getValue().getText(), 60)));

        TableColumn<Question, String> aCol = col("A", 90);
        aCol.setCellValueFactory(d -> new SimpleStringProperty(truncate(d.getValue().getOptionA(), 20)));
        TableColumn<Question, String> bCol = col("B", 90);
        bCol.setCellValueFactory(d -> new SimpleStringProperty(truncate(d.getValue().getOptionB(), 20)));
        TableColumn<Question, String> cCol = col("C", 90);
        cCol.setCellValueFactory(d -> new SimpleStringProperty(truncate(d.getValue().getOptionC(), 20)));
        TableColumn<Question, String> dCol = col("D", 90);
        dCol.setCellValueFactory(d -> new SimpleStringProperty(truncate(d.getValue().getOptionD(), 20)));

        TableColumn<Question, String> ansCol = col("Answer", 70);
        ansCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCorrectAnswer())));
        ansCol.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setTextFill(Color.web(UITheme.ACCENT_GREEN));
                setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
            }
        });

        TableColumn<Question, String> marksCol = col("Marks", 60);
        marksCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getMarks())));

        TableColumn<Question, Void> delCol = new TableColumn<>("Del");
        delCol.setMaxWidth(60);
        delCol.setCellFactory(c -> new TableCell<>() {
            private final Button del = smallBtn("✕", UITheme.ACCENT_DANGER);
            { del.setOnAction(e -> {
                Question q = getTableView().getItems().get(getIndex());
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete this question?", ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText("Confirm");
                confirm.showAndWait().ifPresent(r -> {
                    if (r == ButtonType.YES) {
                        exam.removeQuestion(q);
                        DataStore.get().updateExam(exam);
                        table.setItems(FXCollections.observableArrayList(exam.getQuestions()));
                        examTable.refresh();
                    }
                });
            }); }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : del);
            }
        });

        table.getColumns().addAll(numCol, qCol, aCol, bCol, cCol, dCol, ansCol, marksCol, delCol);
        table.setItems(FXCollections.observableArrayList(exam.getQuestions()));
        VBox.setVgrow(table, Priority.ALWAYS);

        // Footer
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));
        Label info = UITheme.muted(exam.getQuestions().size() + " question(s)  ·  Total marks: " + exam.getTotalMarks());
        Region fSpacer = new Region();
        HBox.setHgrow(fSpacer, Priority.ALWAYS);
        Button closeBtn = UITheme.ghostButton("Close");
        closeBtn.setOnAction(e -> dialog.close());
        footer.getChildren().addAll(info, fSpacer, closeBtn);

        bp.setTop(header);
        bp.setCenter(table);
        bp.setBottom(footer);

        Scene scene = new Scene(bp, 860, 560);
        LoginScreen.applyCSS(scene);
        dialog.setScene(scene);
        dialog.show();
    }

    private void openAddQuestionDialog(Exam exam, Stage parentDialog, TableView<Exam> examTable) {
        Stage dialog = new Stage();
        dialog.setTitle("Add Question");
        dialog.initOwner(parentDialog);

        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #162030;");
        panel.setPadding(new Insets(22, 28, 22, 28));

        Label heading = UITheme.heading("Add Question");
        VBox.setMargin(heading, new Insets(0, 0, 14, 0));

        TextField qText  = UITheme.textField("Question text");
        TextField optA   = UITheme.textField("Option A");
        TextField optB   = UITheme.textField("Option B");
        TextField optC   = UITheme.textField("Option C");
        TextField optD   = UITheme.textField("Option D");
        TextField marksF = UITheme.textField("Marks (default: 1)");
        for (TextField tf : new TextField[]{qText, optA, optB, optC, optD, marksF}) {
            tf.setMaxWidth(Double.MAX_VALUE);
        }

        ComboBox<String> answerCombo = new ComboBox<>(FXCollections.observableArrayList("A", "B", "C", "D"));
        answerCombo.getSelectionModel().selectFirst();
        answerCombo.setMaxWidth(Double.MAX_VALUE);

        Label errLbl = UITheme.errorLabel();

        Button saveBtn = UITheme.primaryButton("Add Question");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            String qt = qText.getText().trim();
            String a = optA.getText().trim(), b = optB.getText().trim();
            String c = optC.getText().trim(), d = optD.getText().trim();
            if (qt.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
                errLbl.setText("All fields are required."); return;
            }
            int m = 1;
            try {
                if (!marksF.getText().trim().isEmpty()) m = Integer.parseInt(marksF.getText().trim());
            } catch (NumberFormatException ex) {
                errLbl.setText("Marks must be a number."); return;
            }
            char correct = answerCombo.getValue().charAt(0);
            exam.addQuestion(new Question(qt, a, b, c, d, correct, m));
            DataStore.get().updateExam(exam);
            dialog.close();
            // Refresh parent dialog by reopening it
            examTable.refresh();
        });

        panel.getChildren().addAll(
            heading,
            UITheme.fieldLabel("Question Text"), qText,
            UITheme.fieldLabel("Option A"),      optA,
            UITheme.fieldLabel("Option B"),      optB,
            UITheme.fieldLabel("Option C"),      optC,
            UITheme.fieldLabel("Option D"),      optD,
            UITheme.fieldLabel("Correct Answer"),answerCombo,
            UITheme.fieldLabel("Marks"),         marksF,
            errLbl, saveBtn
        );
        for (int i = 1; i < panel.getChildren().size(); i++) {
            VBox.setMargin(panel.getChildren().get(i), new Insets(4, 0, 0, 0));
        }
        VBox.setMargin(saveBtn, new Insets(14, 0, 0, 0));

        ScrollPane sp = new ScrollPane(panel);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");

        Scene scene = new Scene(sp, 500, 560);
        LoginScreen.applyCSS(scene);
        dialog.setScene(scene);
        dialog.show();
    }

    private void refresh() {
        AdminDashboardScreen fresh = new AdminDashboardScreen(stage);
        Scene scene = new Scene(fresh.getRoot(), stage.getScene().getWidth(), stage.getScene().getHeight());
        LoginScreen.applyCSS(scene);
        stage.setScene(scene);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private <S, T> TableColumn<S, T> col(String title, double width) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setSortable(false);
        return col;
    }

    private Button smallBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
        btn.setStyle(String.format("""
            -fx-background-color: #1C2B3A;
            -fx-text-fill: %s;
            -fx-background-radius: 6;
            -fx-border-color: #243447;
            -fx-border-radius: 6;
            -fx-padding: 3 8 3 8;
            -fx-cursor: hand;
            """, color));
        return btn;
    }

    private String truncate(String s, int max) {
        return s != null && s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
