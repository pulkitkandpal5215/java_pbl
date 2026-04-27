package com.examgui;

import com.examgui.ui.LoginScreen;
import com.examgui.util.UITheme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
* JavaFX Online Examination System — Entry point.
*/
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        LoginScreen login = new LoginScreen(primaryStage);
        Scene scene = new Scene(login.getRoot(), 480, 600);
        scene.getStylesheets().add("data:text/css," +
        UITheme.GLOBAL_CSS.replace("\n", " ").replace("\"", "'"));
        primaryStage.setTitle("ExamPortal — Login");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
