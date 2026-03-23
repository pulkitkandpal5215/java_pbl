package com.examgui;

import com.examgui.ui.LoginFrame;
import com.examgui.util.UITheme;
import javax.swing.*;

/**
 * 🎓 Online Examination System — Java Swing GUI
 * Entry point. Sets look-and-feel and launches the login screen.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.applyGlobalTheme();
            new LoginFrame().setVisible(true);
        });
    }
}
