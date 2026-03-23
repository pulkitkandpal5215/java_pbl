package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 📝 Exam Window
 * The live exam-taking interface.
 * Shows one question at a time with A/B/C/D options, a countdown timer,
 * and a question navigation sidebar.
 */
public class ExamWindow extends JFrame {

    private final Exam exam;
    private final ExamAttempt attempt;
    private final JFrame parent;
    private final List<Question> questions;

    private int currentIndex = 0;
    private final Map<Integer, Character> answers = new HashMap<>(); // qIndex → answer
    private Timer countdownTimer;
    private int secondsLeft;

    // UI components updated dynamically
    private JLabel timerLabel;
    private JLabel questionCounter;
    private JLabel questionText;
    private ButtonGroup optionGroup;
    private JRadioButton[] optionBtns;
    private JPanel navPanel;
    private JButton prevBtn, nextBtn, submitBtn;

    public ExamWindow(Exam exam, ExamAttempt attempt, JFrame parent) {
        this.exam = exam;
        this.attempt = attempt;
        this.parent = parent;
        this.questions = exam.getQuestions();
        this.secondsLeft = exam.getDurationMinutes() * 60;

        setTitle(exam.getTitle());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setResizable(true);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmExit(); }
        });

        setContentPane(buildUI());
        startTimer();
        loadQuestion(0);
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        // ── Top header bar ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(12, 24, 12, 24)
        ));

        JLabel examTitle = new JLabel("📝  " + exam.getTitle());
        examTitle.setFont(UITheme.FONT_HEADING);
        examTitle.setForeground(UITheme.TEXT_PRIMARY);

        timerLabel = new JLabel("⏱  --:--");
        timerLabel.setFont(UITheme.FONT_HEADING);
        timerLabel.setForeground(UITheme.ACCENT);

        questionCounter = new JLabel("Question 1 of " + questions.size());
        questionCounter.setFont(UITheme.FONT_SMALL);
        questionCounter.setForeground(UITheme.TEXT_MUTED);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        headerRight.setOpaque(false);
        headerRight.add(questionCounter);
        headerRight.add(timerLabel);

        header.add(examTitle, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        // ── Main layout ───────────────────────────────────────────────────────
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(UITheme.BG_DARK);

        // LEFT: Question + Options
        JPanel questionArea = buildQuestionArea();

        // RIGHT: Navigator sidebar
        JPanel sidebar = buildSidebar();

        main.add(questionArea, BorderLayout.CENTER);
        main.add(sidebar, BorderLayout.EAST);

        // ── Bottom nav ────────────────────────────────────────────────────────
        JPanel bottom = buildBottomBar();

        root.add(header, BorderLayout.NORTH);
        root.add(main, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildQuestionArea() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_DARK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 40, 20, 30));

        // Question number badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badge.setOpaque(false);
        JLabel num = new JLabel("Q" + (currentIndex + 1));
        num.setFont(UITheme.FONT_SMALL);
        num.setForeground(UITheme.ACCENT);
        num.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.ACCENT, 1, 6),
            new EmptyBorder(2, 8, 2, 8)
        ));
        badge.add(num);

        // Question text
        questionText = new JLabel("<html><body style='width:520px'>" + "Loading..." + "</body></html>");
        questionText.setFont(new Font("SansSerif", Font.PLAIN, 16));
        questionText.setForeground(UITheme.TEXT_PRIMARY);
        questionText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Options
        optionGroup = new ButtonGroup();
        optionBtns = new JRadioButton[4];
        char[] labels = {'A', 'B', 'C', 'D'};
        JPanel optionsPanel = new JPanel();
        optionsPanel.setOpaque(false);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionBtns[i] = createOptionButton(labels[i], "");
            optionBtns[i].addActionListener(e -> {
                answers.put(currentIndex, labels[idx]);
                updateNavButtons();
            });
            optionGroup.add(optionBtns[i]);
            optionsPanel.add(optionBtns[i]);
            optionsPanel.add(Box.createVerticalStrut(8));
        }

        panel.add(badge);
        panel.add(Box.createVerticalStrut(14));
        panel.add(questionText);
        panel.add(Box.createVerticalStrut(28));
        panel.add(optionsPanel);

        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(null);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return (JPanel) panel.getParent() != null ? panel : wrapInScroll(panel);
    }

    private JPanel wrapInScroll(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.BG_DARK);
        JScrollPane sp = new JScrollPane(inner);
        sp.setBorder(null);
        sp.setBackground(UITheme.BG_DARK);
        sp.getViewport().setBackground(UITheme.BG_DARK);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private JRadioButton createOptionButton(char label, String text) {
        JRadioButton btn = new JRadioButton(label + ".  " + text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected()) {
                    g2.setColor(new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(),
                        UITheme.ACCENT.getBlue(), 30));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255,255,255, 8));
                } else {
                    g2.setColor(UITheme.BG_INPUT);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (isSelected()) {
                    g2.setColor(new Color(UITheme.ACCENT.getRed(), UITheme.ACCENT.getGreen(),
                        UITheme.ACCENT.getBlue(), 80));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                }
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setMaximumSize(new Dimension(600, 52));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_CARD);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, UITheme.BORDER),
            new EmptyBorder(20, 16, 20, 16)
        ));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel navTitle = UITheme.muted("Question Navigator");
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navTitle);
        sidebar.add(Box.createVerticalStrut(12));

        // Legend
        JPanel legend = new JPanel(new GridLayout(3, 1, 0, 4));
        legend.setOpaque(false);
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        legend.add(legendItem(UITheme.ACCENT_GREEN, "Answered"));
        legend.add(legendItem(UITheme.TEXT_MUTED, "Not answered"));
        legend.add(legendItem(UITheme.ACCENT, "Current"));
        legend.setMaximumSize(new Dimension(180, 80));
        sidebar.add(legend);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(UITheme.separator());
        sidebar.add(Box.createVerticalStrut(14));

        // Question number grid
        navPanel = new JPanel(new GridLayout(0, 5, 6, 6));
        navPanel.setOpaque(false);
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateNavButtons();
        sidebar.add(navPanel);

        return sidebar;
    }

    private JLabel legendItem(Color color, String text) {
        JLabel l = new JLabel("●  " + text);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(color);
        return l;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            new EmptyBorder(12, 24, 12, 24)
        ));

        prevBtn = UITheme.ghostButton("← Previous");
        prevBtn.addActionListener(e -> navigate(-1));

        nextBtn = UITheme.primaryButton("Next →");
        nextBtn.addActionListener(e -> navigate(1));

        submitBtn = UITheme.warningButton("Submit Exam");
        submitBtn.addActionListener(e -> confirmSubmit());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(prevBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(nextBtn);
        right.add(submitBtn);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Logic ─────────────────────────────────────────────────────────────────

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;
        currentIndex = index;
        Question q = questions.get(index);

        questionText.setText("<html><body style='width:520px; font-size:14px'>" + q.getText() + "</body></html>");
        questionCounter.setText("Question " + (index + 1) + " of " + questions.size());

        char[] labels = {'A', 'B', 'C', 'D'};
        String[] opts = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
        for (int i = 0; i < 4; i++) {
            optionBtns[i].setText(labels[i] + ".  " + opts[i]);
            optionBtns[i].setSelected(false);
        }

        // Restore previous answer if any
        Character prev = answers.get(currentIndex);
        if (prev != null) {
            int btnIdx = prev - 'A';
            if (btnIdx >= 0 && btnIdx < 4) optionBtns[btnIdx].setSelected(true);
        }

        prevBtn.setEnabled(index > 0);
        nextBtn.setEnabled(index < questions.size() - 1);
        updateNavButtons();
    }

    private void navigate(int dir) {
        loadQuestion(currentIndex + dir);
    }

    private void updateNavButtons() {
        if (navPanel == null) return;
        navPanel.removeAll();
        for (int i = 0; i < questions.size(); i++) {
            final int qi = i;
            boolean answered  = answers.containsKey(i);
            boolean isCurrent = (i == currentIndex);
            JButton nb = new JButton(String.valueOf(i + 1)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color bg = isCurrent ? UITheme.ACCENT :
                               answered  ? new Color(UITheme.ACCENT_GREEN.getRed(),
                                   UITheme.ACCENT_GREEN.getGreen(), UITheme.ACCENT_GREEN.getBlue(), 120) :
                               UITheme.BG_INPUT;
                    g2.setColor(bg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2.setColor(isCurrent ? UITheme.BG_DARK : answered ? UITheme.ACCENT_GREEN : UITheme.TEXT_MUTED);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = getText();
                    g2.drawString(t, (getWidth() - fm.stringWidth(t))/2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            };
            nb.setPreferredSize(new Dimension(32, 32));
            nb.setContentAreaFilled(false);
            nb.setBorderPainted(false);
            nb.setFocusPainted(false);
            nb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            nb.addActionListener(e -> loadQuestion(qi));
            navPanel.add(nb);
        }
        navPanel.revalidate();
        navPanel.repaint();
    }

    private void startTimer() {
        countdownTimer = new Timer(1000, e -> {
            secondsLeft--;
            int min = secondsLeft / 60, sec = secondsLeft % 60;
            timerLabel.setText(String.format("⏱  %02d:%02d", min, sec));

            if (secondsLeft <= 300) timerLabel.setForeground(UITheme.ACCENT_WARN);
            if (secondsLeft <= 60)  timerLabel.setForeground(UITheme.ACCENT_DANGER);

            if (secondsLeft <= 0) {
                countdownTimer.stop();
                JOptionPane.showMessageDialog(ExamWindow.this,
                    "Time's up! Your exam is being submitted.", "Time Over",
                    JOptionPane.WARNING_MESSAGE);
                doSubmit();
            }
        });
        countdownTimer.start();
    }

    private void confirmSubmit() {
        int answered  = answers.size();
        int total     = questions.size();
        int unanswered = total - answered;

        String msg = "<html><b>Submit Exam?</b><br><br>" +
            "Answered: " + answered + " / " + total + " questions<br>";
        if (unanswered > 0) msg += "<font color='#FF5F5F'>Unanswered: " + unanswered + " (will be marked 0)</font><br>";
        msg += "<br>Once submitted, you cannot change your answers.</html>";

        int res = JOptionPane.showConfirmDialog(this, msg,
            "Confirm Submission", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) doSubmit();
    }

    private void doSubmit() {
        if (countdownTimer != null) countdownTimer.stop();

        // Grade the exam
        int score = 0, totalMarks = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            totalMarks += q.getMarks();
            Character selected = answers.get(i);
            if (selected != null && selected == q.getCorrectAnswer()) {
                score += q.getMarks();
            }
            // Record in attempt
            if (selected != null) attempt.recordAnswer(q.getId(), selected);
        }

        double pct = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0;
        boolean passed = pct >= exam.getPassingScore();
        attempt.submit(score, totalMarks, passed);

        dispose();
        new ResultFrame(exam, attempt, questions, answers, parent).setVisible(true);
    }

    private void confirmExit() {
        int res = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit? Your progress will be lost!",
            "Exit Exam", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            if (countdownTimer != null) countdownTimer.stop();
            DataStore.get().getAttemptsForStudent(attempt.getStudentId())
                .removeIf(a -> a.getId() == attempt.getId());
            dispose();
            parent.setVisible(true);
        }
    }
}
