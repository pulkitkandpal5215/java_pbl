package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.util.UITheme;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class RegisterScreen {

    private final Stage stage;
    private final VBox root;

    private TextField nameField, emailField;
    private PasswordField passField, confirmField;
    private Label statusLabel;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
        this.root = build();
    }

    public VBox getRoot() { return root; }

    private VBox build() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F1923, #0A1628);");
        root.setAlignment(Pos.TOP_CENTER);

        // Header
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(40, 40, 20, 40));
        Label logo = new Label("🎓 ExamPortal");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 28));
        logo.setTextFill(Color.web(UITheme.ACCENT));
        header.getChildren().add(logo);

        // Card
        VBox card = new VBox(0);
        card.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447;
            -fx-border-radius: 16;
            -fx-background-radius: 16;
            """);
        card.setPadding(new Insets(28, 36, 28, 36));
        card.setMaxWidth(400);

        Label title = new Label("Create Account");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 20));
        title.setTextFill(Color.web(UITheme.TEXT_PRIMARY));

        nameField    = UITheme.textField("Full name");
        emailField   = UITheme.textField("Email address");
        passField    = UITheme.passwordField("Password (min 6 chars)");
        confirmField = UITheme.passwordField("Confirm password");
        for (Control c : new Control[]{nameField, emailField, passField, confirmField}) {
            c.setMaxWidth(Double.MAX_VALUE);
        }

        statusLabel = UITheme.errorLabel();

        Button registerBtn = UITheme.primaryButton("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setOnAction(e -> doRegister());

        Separator sep = UITheme.separator();

        HBox backRow = new HBox(4);
        backRow.setAlignment(Pos.CENTER);
        Label already = UITheme.muted("Already have an account?");
        Hyperlink signIn = new Hyperlink("Sign In");
        signIn.setStyle("-fx-text-fill: #00C9A7; -fx-border-color: transparent;");
        signIn.setOnAction(e -> openLogin());
        backRow.getChildren().addAll(already, signIn);

        VBox.setMargin(title,       new Insets(0, 0, 18, 0));
        VBox.setMargin(nameField,   new Insets(4, 0, 0, 0));
        VBox.setMargin(emailField,  new Insets(4, 0, 0, 0));
        VBox.setMargin(passField,   new Insets(4, 0, 0, 0));
        VBox.setMargin(confirmField,new Insets(4, 0, 0, 0));
        VBox.setMargin(registerBtn, new Insets(14, 0, 0, 0));
        VBox.setMargin(sep,         new Insets(16, 0, 12, 0));

        card.getChildren().addAll(
            title,
            UITheme.fieldLabel("Full Name"),    nameField,
            UITheme.fieldLabel("Email"),        emailField,
            UITheme.fieldLabel("Password"),     passField,
            UITheme.fieldLabel("Confirm Password"), confirmField,
            statusLabel,
            registerBtn,
            sep,
            backRow
        );

        VBox.setMargin(card, new Insets(0, 40, 40, 40));
        root.getChildren().addAll(header, card);
        return root;
    }

    private void doRegister() {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass  = passField.getText();
        String conf  = confirmField.getText();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("All fields are required."); return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            statusLabel.setText("Enter a valid email address."); return;
        }
        if (pass.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters."); return;
        }
        if (!pass.equals(conf)) {
            statusLabel.setText("Passwords do not match."); return;
        }

        boolean ok = DataStore.get().registerStudent(name, email, pass);
        if (!ok) {
            statusLabel.setText("Email already registered."); return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION,
            "Account created! Please sign in.", ButtonType.OK);
        alert.setHeaderText("Success");
        alert.showAndWait();
        openLogin();
    }

    private void openLogin() {
        LoginScreen login = new LoginScreen(stage);
        Scene scene = new Scene(login.getRoot(), 480, 600);
        LoginScreen.applyCSS(scene);
        stage.setTitle("ExamPortal — Login");
        stage.setResizable(false);
        stage.setScene(scene);
    }
}
