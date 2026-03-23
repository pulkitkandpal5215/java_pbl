package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * 🎓 Student Dashboard
 * Shows available exams and exam history.
 * Tab-based layout: Available Exams | My Results
 */
public class StudentDashboard extends JFrame {

    private final User student = DataStore.get().getCurrentUser();

    public StudentDashboard() {
        setTitle("ExamPortal — " + student.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 640);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        // ── Top Nav Bar ───────────────────────────────────────────────────────
        JPanel nav = buildNavBar();
        root.add(nav, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);
        tabs.setUI(new StyledTabbedPaneUI());

        tabs.addTab("  📚  Available Exams  ", buildExamListPanel());
        tabs.addTab("  📊  My Results       ", buildResultsPanel());

        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.BG_CARD);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(10, 24, 10, 24)
        ));

        JLabel brand = new JLabel("🎓 ExamPortal");
        brand.setFont(UITheme.FONT_HEADING);
        brand.setForeground(UITheme.ACCENT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel userLabel = new JLabel("👤 " + student.getName());
        userLabel.setForeground(UITheme.TEXT_MUTED);
        userLabel.setFont(UITheme.FONT_SMALL);

        JButton logoutBtn = UITheme.ghostButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(90, 32));
        logoutBtn.addActionListener(e -> {
            DataStore.get().logout();
            dispose();
            new LoginFrame().setVisible(true);
        });

        right.add(userLabel);
        right.add(logoutBtn);
        nav.add(brand, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    private JScrollPane buildExamListPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        List<Exam> exams = DataStore.get().getPublishedExams();

        if (exams.isEmpty()) {
            JLabel empty = new JLabel("No exams available right now.");
            empty.setForeground(UITheme.TEXT_MUTED);
            empty.setFont(UITheme.FONT_BODY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalGlue());
            panel.add(empty);
            panel.add(Box.createVerticalGlue());
        } else {
            JLabel heading = UITheme.heading("Available Exams");
            heading.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(heading);
            panel.add(Box.createVerticalStrut(4));
            JLabel sub = UITheme.muted(exams.size() + " exam(s) ready for you");
            sub.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(sub);
            panel.add(Box.createVerticalStrut(20));

            for (Exam exam : exams) {
                panel.add(buildExamCard(exam));
                panel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    private JPanel buildExamCard(Exam exam) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.BORDER, 1, 12),
            new EmptyBorder(18, 20, 18, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Left info
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(exam.getTitle());
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel desc = new JLabel(exam.getDescription() != null ? exam.getDescription() : "");
        desc.setFont(UITheme.FONT_SMALL);
        desc.setForeground(UITheme.TEXT_MUTED);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        meta.setOpaque(false);
        meta.add(chip("⏱ " + exam.getDurationMinutes() + " min", UITheme.BG_INPUT));
        meta.add(chip("❓ " + exam.getQuestions().size() + " questions", UITheme.BG_INPUT));
        meta.add(chip("✅ Pass: " + exam.getPassingScore() + "%", UITheme.BG_INPUT));

        info.add(title);
        info.add(Box.createVerticalStrut(4));
        info.add(desc);
        info.add(Box.createVerticalStrut(8));
        info.add(meta);

        // Right button
        JButton startBtn = UITheme.primaryButton("Start Exam →");
        startBtn.setPreferredSize(new Dimension(140, 42));
        startBtn.addActionListener(e -> startExam(exam));

        card.add(info, BorderLayout.CENTER);
        card.add(startBtn, BorderLayout.EAST);
        return card;
    }

    private JScrollPane buildResultsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        List<ExamAttempt> attempts = DataStore.get()
            .getAttemptsForStudent(student.getId())
            .stream()
            .filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED)
            .toList();

        JLabel heading = UITheme.heading("My Exam Results");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));
        JLabel sub = UITheme.muted(attempts.size() + " completed exam(s)");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sub);
        panel.add(Box.createVerticalStrut(20));

        if (attempts.isEmpty()) {
            JLabel empty = UITheme.muted("You haven't completed any exams yet.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(40));
            panel.add(empty);
        } else {
            for (ExamAttempt attempt : attempts) {
                panel.add(buildResultCard(attempt));
                panel.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    private JPanel buildResultCard(ExamAttempt attempt) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(attempt.isPassed() ?
                new Color(UITheme.ACCENT_GREEN.getRed(), UITheme.ACCENT_GREEN.getGreen(),
                    UITheme.ACCENT_GREEN.getBlue(), 80) :
                new Color(UITheme.ACCENT_DANGER.getRed(), UITheme.ACCENT_DANGER.getGreen(),
                    UITheme.ACCENT_DANGER.getBlue(), 80), 1, 12),
            new EmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(attempt.getExamTitle());
        name.setFont(UITheme.FONT_HEADING);
        name.setForeground(UITheme.TEXT_PRIMARY);

        JLabel meta = new JLabel(String.format("Score: %d / %d  ·  Submitted: %s",
            attempt.getScore(), attempt.getTotalMarks(),
            attempt.getSubmittedAt() != null ?
                attempt.getSubmittedAt().toLocalDate().toString() : "—"));
        meta.setFont(UITheme.FONT_SMALL);
        meta.setForeground(UITheme.TEXT_MUTED);

        left.add(name);
        left.add(Box.createVerticalStrut(4));
        left.add(meta);

        // Score pill
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        String pct = String.format("%.1f%%", attempt.getPercentage());
        Color pillColor = attempt.isPassed() ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;
        JLabel pill = new JLabel(pct + (attempt.isPassed() ? "  PASS" : "  FAIL")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(pillColor.getRed(), pillColor.getGreen(), pillColor.getBlue(), 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        pill.setFont(UITheme.FONT_HEADING);
        pill.setForeground(pillColor);
        pill.setBorder(new EmptyBorder(4, 14, 4, 14));
        pill.setOpaque(false);
        right.add(pill);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JLabel chip(String text, Color bg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setBorder(new EmptyBorder(3, 10, 3, 10));
        l.setOpaque(false);
        return l;
    }

    private void startExam(Exam exam) {
        if (DataStore.get().hasActiveAttempt(student.getId(), exam.getId())) {
            JOptionPane.showMessageDialog(this,
                "You already have an attempt in progress for this exam.",
                "Active Attempt", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>" + exam.getTitle() + "</b><br><br>" +
            "Duration: " + exam.getDurationMinutes() + " minutes<br>" +
            "Questions: " + exam.getQuestions().size() + "<br>" +
            "Passing Score: " + exam.getPassingScore() + "%<br><br>" +
            "Are you ready to start?</html>",
            "Start Exam", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ExamAttempt attempt = new ExamAttempt(student.getId(), exam.getId(), exam.getTitle());
            DataStore.get().addAttempt(attempt);
            new ExamWindow(exam, attempt, this).setVisible(true);
            setVisible(false);
        }
    }

    /** Custom TabbedPane UI */
    private static class StyledTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {
        @Override protected void paintTabBackground(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            if (isSelected) {
                g2.setColor(UITheme.BG_DARK);
            } else {
                g2.setColor(UITheme.BG_CARD);
            }
            g2.fillRect(x, y, w, h);
        }
        @Override protected void paintTabBorder(Graphics g, int tabPlacement,
                int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            if (isSelected) {
                g.setColor(UITheme.ACCENT);
                g.fillRect(x, y + h - 3, w, 3);
            }
        }
        @Override protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {}
    }
}
