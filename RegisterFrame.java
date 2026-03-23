package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passField, confirmField;
    private JLabel statusLabel;

    public RegisterFrame() {
        setTitle("ExamPortal — Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK,
                    getWidth(), getHeight(), new Color(0x0A1628));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(40, 40, 20, 40));
        JLabel logo = new JLabel("🎓 ExamPortal");
        logo.setFont(UITheme.FONT_TITLE);
        logo.setForeground(UITheme.ACCENT);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(logo);

        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.BORDER, 1, 16),
            new EmptyBorder(28, 36, 28, 36)
        ));

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField    = UITheme.textField("Full name");
        emailField   = UITheme.textField("Email address");
        passField    = UITheme.passwordField("Password (min 6 chars)");
        confirmField = UITheme.passwordField("Confirm password");
        for (JComponent c : new JComponent[]{nameField, emailField, passField, confirmField}) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        JButton registerBtn = UITheme.primaryButton("Create Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> doRegister());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.ACCENT_DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backRow.setOpaque(false);
        JLabel already = new JLabel("Already have an account?");
        already.setForeground(UITheme.TEXT_MUTED);
        already.setFont(UITheme.FONT_SMALL);
        JButton backBtn = new JButton("Sign In");
        backBtn.setFont(UITheme.FONT_SMALL);
        backBtn.setForeground(UITheme.ACCENT);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        backRow.add(already);
        backRow.add(backBtn);

        card.add(title);
        card.add(Box.createVerticalStrut(22));
        card.add(lbl("Full Name"));
        card.add(Box.createVerticalStrut(4));
        card.add(nameField);
        card.add(Box.createVerticalStrut(12));
        card.add(lbl("Email"));
        card.add(Box.createVerticalStrut(4));
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));
        card.add(lbl("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passField);
        card.add(Box.createVerticalStrut(12));
        card.add(lbl("Confirm Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(6));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(UITheme.separator());
        card.add(Box.createVerticalStrut(12));
        card.add(backRow);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 40, 40, 40));
        center.add(card, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        return root;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void doRegister() {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword());
        String conf  = new String(confirmField.getPassword());

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

        JOptionPane.showMessageDialog(this, "Account created! Please sign in.", "Success",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame().setVisible(true);
    }
}
