package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.User;
import com.examgui.util.UITheme;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * JavaFX Login Screen.
 */
public class LoginScreen {

    private final Stage stage;
    private final VBox root;

    private TextField emailField;
    private PasswordField passField;
    private Label statusLabel;

    public LoginScreen(Stage stage) {
        this.stage = stage;
        this.root = build();
    }

    public VBox getRoot() { return root; }

    private VBox build() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F1923, #0A1628);");
        root.setAlignment(Pos.TOP_CENTER);

        // ── Header ────────────────────────────────────────────────────────────
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(50, 40, 30, 40));

        Label logo = new Label("🎓 ExamPortal");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web(UITheme.ACCENT));

        Label sub = UITheme.muted("Online Examination System");

        header.getChildren().addAll(logo, sub);

        // ── Card ──────────────────────────────────────────────────────────────
        VBox card = new VBox(0);
        card.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447;
            -fx-border-radius: 16;
            -fx-background-radius: 16;
            """);
        card.setPadding(new Insets(30, 36, 30, 36));
        card.setMaxWidth(400);

        Label title = UITheme.heading("Welcome back");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        Label hint = UITheme.muted("Sign in to continue");

        // Fields
        emailField = UITheme.textField("you@example.com");
        emailField.setMaxWidth(Double.MAX_VALUE);
        passField = UITheme.passwordField("••••••••");
        passField.setMaxWidth(Double.MAX_VALUE);
        passField.setOnAction(e -> doLogin());

        statusLabel = UITheme.errorLabel();

        Button loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> doLogin());

        Separator sep = UITheme.separator();

        // Register row
        HBox regRow = new HBox(4);
        regRow.setAlignment(Pos.CENTER);
        Label newUser = UITheme.muted("Don't have an account?");
        Hyperlink regLink = new Hyperlink("Register");
        regLink.setStyle("-fx-text-fill: #00C9A7; -fx-border-color: transparent;");
        regLink.setOnAction(e -> openRegister());
        regRow.getChildren().addAll(newUser, regLink);

        // Demo box
        VBox demoBox = new VBox(4);
        demoBox.setStyle("""
            -fx-background-color: rgba(0,201,167,0.08);
            -fx-border-color: rgba(0,201,167,0.3);
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            """);
        demoBox.setPadding(new Insets(10, 14, 10, 14));
        Label demoTitle = new Label("Demo Accounts");
        demoTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
        demoTitle.setTextFill(Color.web(UITheme.ACCENT));
        Label d1 = UITheme.muted("Admin: admin@exam.com / admin123");
        Label d2 = UITheme.muted("Student: pulkit@gmail.com / pulkit123");
        demoBox.getChildren().addAll(demoTitle, d1, d2);

        // Assemble card
        VBox.setMargin(title,    new Insets(0, 0, 2, 0));
        VBox.setMargin(hint,     new Insets(0, 0, 20, 0));
        VBox.setMargin(emailField, new Insets(4, 0, 0, 0));
        VBox.setMargin(passField,  new Insets(4, 0, 0, 0));
        VBox.setMargin(loginBtn,   new Insets(14, 0, 0, 0));
        VBox.setMargin(sep,        new Insets(16, 0, 14, 0));
        VBox.setMargin(regRow,     new Insets(0, 0, 12, 0));

        card.getChildren().addAll(
            title, hint,
            UITheme.fieldLabel("Email"), emailField,
            UITheme.fieldLabel("Password"), passField,
            statusLabel,
            loginBtn,
            sep,
            regRow,
            demoBox
        );

        VBox.setMargin(card, new Insets(0, 40, 40, 40));
        VBox.setVgrow(card, Priority.NEVER);

        root.getChildren().addAll(header, card);
        return root;
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = passField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        User user = DataStore.get().login(email, pass);
        if (user == null) {
            statusLabel.setText("Invalid email or password.");
            passField.clear();
            return;
        }

        DataStore.get().setCurrentUser(user);

        if (user.isAdmin()) {
            AdminDashboardScreen admin = new AdminDashboardScreen(stage);
            Scene scene = new Scene(admin.getRoot(), 1100, 700);
            applyCSS(scene);
            stage.setTitle("ExamPortal — Admin Panel");
            stage.setResizable(true);
            stage.setScene(scene);
        } else {
            showStudentDashboard();
        }
    }

    private void showStudentDashboard() {
        StudentDashboardScreen sd = new StudentDashboardScreen(stage);
        Scene scene = new Scene(sd.getRoot(), 900, 640);
        applyCSS(scene);
        stage.setTitle("ExamPortal — " + DataStore.get().getCurrentUser().getName());
        stage.setResizable(true);
        stage.setScene(scene);
    }

    private void openRegister() {
        RegisterScreen reg = new RegisterScreen(stage);
        Scene scene = new Scene(reg.getRoot(), 460, 580);
        applyCSS(scene);
        stage.setTitle("ExamPortal — Register");
        stage.setResizable(false);
        stage.setScene(scene);
    }

    public static void applyCSS(Scene scene) {
        scene.getStylesheets().add("data:text/css," +
            com.examgui.util.UITheme.GLOBAL_CSS.replace("\n", " ").replace("\"", "'"));
    }
}
