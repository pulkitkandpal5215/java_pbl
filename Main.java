package com.examgui;

import com.examgui.ui.LoginFrame;
import com.examgui.util.UITheme;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.applyGlobalTheme();
            new LoginFrame().setVisible(true);
        });
    }
}
