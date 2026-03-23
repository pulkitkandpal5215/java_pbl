package com.examgui.ui;

import com.examgui.model.*;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * 🏆 Result Frame
 * Shows score, pass/fail, and full answer-by-answer review after the exam.
 */
public class ResultFrame extends JFrame {

    private final Exam exam;
    private final ExamAttempt attempt;
    private final List<Question> questions;
    private final Map<Integer, Character> answers; // qIndex → selected
    private final JFrame parent;

    public ResultFrame(Exam exam, ExamAttempt attempt, List<Question> questions,
                       Map<Integer, Character> answers, JFrame parent) {
        this.exam = exam;
        this.attempt = attempt;
        this.questions = questions;
        this.answers = answers;
        this.parent = parent;

        setTitle("Exam Result — " + exam.getTitle());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 700);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildReview(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_CARD);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(28, 40, 28, 40)
        ));

        boolean passed = attempt.isPassed();
        String icon   = passed ? "🎉" : "📝";
        String verdict = passed ? "Congratulations! You Passed!" : "Keep Practicing!";
        Color verdictColor = passed ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel verdictLabel = new JLabel(verdict, SwingConstants.CENTER);
        verdictLabel.setFont(UITheme.FONT_TITLE);
        verdictLabel.setForeground(verdictColor);
        verdictLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel examName = new JLabel(exam.getTitle(), SwingConstants.CENTER);
        examName.setFont(UITheme.FONT_BODY);
        examName.setForeground(UITheme.TEXT_MUTED);
        examName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stats row
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.CENTER_ALIGNMENT);
        stats.add(statBox("Score",
            attempt.getScore() + " / " + attempt.getTotalMarks(), UITheme.ACCENT));
        stats.add(statBox("Percentage",
            String.format("%.1f%%", attempt.getPercentage()), verdictColor));
        stats.add(statBox("Passing",
            exam.getPassingScore() + "%", UITheme.TEXT_MUTED));
        stats.add(statBox("Questions",
            questions.size() + "", UITheme.TEXT_MUTED));

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(verdictLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(examName);
        panel.add(Box.createVerticalStrut(22));
        panel.add(stats);
        return panel;
    }

    private JPanel statBox(String label, String value, Color valueColor) {
        JPanel box = new JPanel();
        box.setBackground(UITheme.BG_INPUT);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.BORDER, 1, 10),
            new EmptyBorder(12, 20, 12, 20)
        ));
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("SansSerif", Font.BOLD, 20));
        val.setForeground(valueColor);
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(val);
        box.add(Box.createVerticalStrut(2));
        box.add(lbl);
        return box;
    }

    private JScrollPane buildReview() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 32, 20, 32));

        JLabel heading = UITheme.heading("Answer Review");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));
        JLabel sub = UITheme.muted("Review each question and the correct answers");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sub);
        panel.add(Box.createVerticalStrut(16));

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Character selected = answers.get(i);
            boolean correct = selected != null && selected == q.getCorrectAnswer();
            panel.add(buildAnswerCard(i + 1, q, selected, correct));
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        return sp;
    }

    private JPanel buildAnswerCard(int num, Question q, Character selected, boolean correct) {
        Color borderColor = selected == null ? UITheme.TEXT_MUTED :
                           correct ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;

        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(borderColor, 1, 10),
            new EmptyBorder(14, 18, 14, 18)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        // Question number + result icon
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        String resultIcon = selected == null ? "⬜ Skipped" :
                            correct ? "✅ Correct" : "❌ Incorrect";
        Color resultColor = selected == null ? UITheme.TEXT_MUTED :
                            correct ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER;
        JLabel numLabel = new JLabel("Q" + num);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        numLabel.setForeground(UITheme.TEXT_MUTED);
        JLabel resultLabel = new JLabel(resultIcon);
        resultLabel.setFont(UITheme.FONT_SMALL);
        resultLabel.setForeground(resultColor);
        JLabel marksLabel = new JLabel("+" + (correct ? q.getMarks() : 0) + " / " + q.getMarks() + " marks");
        marksLabel.setFont(UITheme.FONT_SMALL);
        marksLabel.setForeground(correct ? UITheme.ACCENT_GREEN : UITheme.TEXT_MUTED);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightSide.setOpaque(false);
        rightSide.add(marksLabel);
        rightSide.add(resultLabel);
        topRow.add(numLabel, BorderLayout.WEST);
        topRow.add(rightSide, BorderLayout.EAST);

        JLabel qText = new JLabel("<html><body style='width:600px'>" + q.getText() + "</body></html>");
        qText.setFont(UITheme.FONT_BODY);
        qText.setForeground(UITheme.TEXT_PRIMARY);

        // Answer comparison
        JPanel ansRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        ansRow.setOpaque(false);

        if (selected != null && !correct) {
            JLabel yours = new JLabel("Your answer: " + selected + ". " + q.getOption(selected));
            yours.setFont(UITheme.FONT_SMALL);
            yours.setForeground(UITheme.ACCENT_DANGER);
            ansRow.add(yours);
        }
        JLabel correct_ = new JLabel("✓ Correct: " + q.getCorrectAnswer() + ". " + q.getOption(q.getCorrectAnswer()));
        correct_.setFont(UITheme.FONT_SMALL);
        correct_.setForeground(UITheme.ACCENT_GREEN);
        ansRow.add(correct_);

        card.add(topRow);
        card.add(Box.createVerticalStrut(8));
        card.add(qText);
        card.add(Box.createVerticalStrut(8));
        card.add(ansRow);
        return card;
    }

    private JPanel buildFooter() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton backBtn = UITheme.primaryButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
            // Refresh parent
            if (parent instanceof StudentDashboard) {
                parent.dispose();
                new StudentDashboard().setVisible(true);
            }
        });

        bar.add(backBtn);
        return bar;
    }
}
