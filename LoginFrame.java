package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.User;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * 🔐 Login Screen
 * First screen — user login or sign in kar sakta hai.
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("ExamPortal — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(580, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK,
                    getWidth(), getHeight(), new Color(0x0A1628));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative teal circle
                g2.setColor(new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(),
                    UITheme.ACCENT.getBlue(), 20));
                g2.fillOval(-80, -80, 300, 300);
                g2.dispose();
            }
        };
        root.setOpaque(false);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(50, 40, 30, 40));

        JLabel logo = new JLabel("🎓 ExamPortal");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Online Examination System");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);

        // ── Form card ─────────────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.BORDER, 1, 16),
            new EmptyBorder(30, 36, 30, 36)
        ));

        JLabel title = new JLabel("Welcome back");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Sign in to continue");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        emailField = UITheme.textField("Email address");
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        passField = UITheme.passwordField("Password");
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Allow Enter key to login
        passField.addActionListener(e -> doLogin());

        JButton loginBtn = UITheme.primaryButton("Sign In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginBtn.addActionListener(e -> doLogin());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.ACCENT_DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = UITheme.separator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setOpaque(false);
        JLabel newUser = new JLabel("Don't have an account?");
        newUser.setForeground(UITheme.TEXT_MUTED);
        newUser.setFont(UITheme.FONT_SMALL);
        JButton regBtn = new JButton("Register");
        regBtn.setFont(UITheme.FONT_SMALL);
        regBtn.setForeground(UITheme.ACCENT);
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> openRegister());
        regRow.add(newUser);
        regRow.add(regBtn);

        // Demo credentials hint
        JPanel demoPanel = new JPanel();
        demoPanel.setOpaque(true);
        demoPanel.setBackground(new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(),
            UITheme.ACCENT.getBlue(), 25));
        demoPanel.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(),
                UITheme.ACCENT.getBlue(), 60), 1, 8),
            new EmptyBorder(8, 12, 8, 12)
        ));
        demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
        demoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        demoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel demoTitle = new JLabel("Demo Accounts");
        demoTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        demoTitle.setForeground(UITheme.ACCENT);
        demoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d1 = new JLabel("Admin: admin@exam.com / admin123");
        d1.setFont(UITheme.FONT_SMALL);
        d1.setForeground(UITheme.TEXT_MUTED);
        d1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d2 = new JLabel("Student: pulkit@gmail.com / pulkit123");
        d2.setFont(UITheme.FONT_SMALL);
        d2.setForeground(UITheme.TEXT_MUTED);
        d2.setAlignmentX(Component.LEFT_ALIGNMENT);

        demoPanel.add(demoTitle);
        demoPanel.add(Box.createVerticalStrut(2));
        demoPanel.add(d1);
        demoPanel.add(d2);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(hint);
        card.add(Box.createVerticalStrut(22));
        card.add(label("Email"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));
        card.add(label("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(18));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(regRow);
        card.add(Box.createVerticalStrut(14));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 40, 40, 40));
        center.setLayout(new BorderLayout());
        center.add(card, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BorderLayout());
        bottom.setBorder(new EmptyBorder(0, 40, 40, 40));
        bottom.add(demoPanel, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        User user = DataStore.get().login(email, pass);
        if (user == null) {
            statusLabel.setText("Invalid email or password.");
            passField.setText("");
            return;
        }

        DataStore.get().setCurrentUser(user);
        dispose();

        if (user.isAdmin()) {
            new AdminDashboard().setVisible(true);
        } else {
            new StudentDashboard().setVisible(true);
        }
    }

    private void openRegister() {
        dispose();
        new RegisterFrame().setVisible(true);
    }
}
