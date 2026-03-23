package com.examgui.ui;

import com.examgui.data.DataStore;
import com.examgui.model.*;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * 🛠️ Admin Dashboard
 * Full exam management: create exams, add questions, view reports, manage students.
 */
public class AdminDashboard extends JFrame {

    private final User admin = DataStore.get().getCurrentUser();

    public AdminDashboard() {
        setTitle("ExamPortal — Admin Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);
        root.add(buildNavBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BODY);

        tabs.addTab("  📋  Manage Exams  ",   buildExamManagementTab());
        tabs.addTab("  👥  Students       ",   buildStudentsTab());
        tabs.addTab("  📊  Reports        ",   buildReportsTab());

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
        JLabel brand = new JLabel("🎓 ExamPortal  —  Admin");
        brand.setFont(UITheme.FONT_HEADING);
        brand.setForeground(UITheme.ACCENT);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        JLabel user = new JLabel("🛡  " + admin.getName());
        user.setForeground(UITheme.TEXT_MUTED);
        user.setFont(UITheme.FONT_SMALL);
        JButton logout = UITheme.ghostButton("Logout");
        logout.setPreferredSize(new Dimension(90, 32));
        logout.addActionListener(e -> {
            DataStore.get().logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        right.add(user);
        right.add(logout);
        nav.add(brand, BorderLayout.WEST);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Tab 1: Exam Management ────────────────────────────────────────────────

    private JPanel buildExamManagementTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header row
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));
        JLabel title = UITheme.heading("All Exams");
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        JButton createBtn = UITheme.primaryButton("+ Create Exam");
        createBtn.addActionListener(e -> openCreateExamDialog());
        btnRow.add(createBtn);
        header.add(title, BorderLayout.WEST);
        header.add(btnRow, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Title", "Questions", "Duration", "Pass %", "Status", "Actions"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        refreshExamTable(model);

        JTable table = buildStyledTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(90);
        table.getColumnModel().getColumn(3).setMaxWidth(90);
        table.getColumnModel().getColumn(4).setMaxWidth(70);
        table.getColumnModel().getColumn(5).setMaxWidth(90);
        table.getColumnModel().getColumn(6).setMinWidth(220);

        // Action buttons renderer
        table.getColumn("Actions").setCellRenderer(new ActionButtonRenderer());

        // Row click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                List<Exam> exams = DataStore.get().getAllExams();
                if (row >= exams.size()) return;
                Exam exam = exams.get(row);

                if (col == 6) {
                    // Determine which "button" was clicked by X position
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relX = e.getX() - cellRect.x;
                    int btnW = cellRect.width / 3;

                    if (relX < btnW) {
                        // Questions
                        openQuestionsDialog(exam);
                    } else if (relX < btnW * 2) {
                        // Publish/Unpublish
                        exam.setPublished(!exam.isPublished());
                        refreshExamTable(model);
                        table.repaint();
                    } else {
                        // Delete
                        int confirm = JOptionPane.showConfirmDialog(AdminDashboard.this,
                            "Delete exam \"" + exam.getTitle() + "\"?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            DataStore.get().removeExam(exam);
                            refreshExamTable(model);
                        }
                    }
                } else if (e.getClickCount() == 2) {
                    openQuestionsDialog(exam);
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 1, 8));
        sp.setBackground(UITheme.BG_CARD);
        sp.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(header, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void refreshExamTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Exam e : DataStore.get().getAllExams()) {
            model.addRow(new Object[]{
                e.getId(), e.getTitle(), e.getQuestions().size(),
                e.getDurationMinutes() + " min", e.getPassingScore() + "%",
                e.isPublished() ? "Published" : "Draft", "actions"
            });
        }
    }

    // ── Tab 2: Students ───────────────────────────────────────────────────────

    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.heading("Registered Students");
        title.setBorder(new EmptyBorder(0, 0, 16, 0));

        String[] cols = {"ID", "Name", "Email", "Exams Taken", "Avg Score"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (User u : DataStore.get().getAllStudents()) {
            List<ExamAttempt> att = DataStore.get().getAttemptsForStudent(u.getId())
                .stream().filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED).toList();
            double avg = att.stream().mapToDouble(ExamAttempt::getPercentage).average().orElse(0.0);
            model.addRow(new Object[]{
                u.getId(), u.getName(), u.getEmail(),
                att.size(), att.isEmpty() ? "—" : String.format("%.1f%%", avg)
            });
        }

        JTable table = buildStyledTable(model);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(3).setMaxWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(100);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 1, 8));
        sp.setBackground(UITheme.BG_CARD);
        sp.getViewport().setBackground(UITheme.BG_CARD);

        panel.add(title, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    // ── Tab 3: Reports ────────────────────────────────────────────────────────

    private JPanel buildReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Summary cards row
        JPanel cards = new JPanel(new GridLayout(1, 4, 16, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(0, 0, 24, 0));

        int totalExams    = DataStore.get().getAllExams().size();
        int published     = (int) DataStore.get().getAllExams().stream().filter(Exam::isPublished).count();
        int totalStudents = DataStore.get().getAllStudents().size();
        long totalAttempts = DataStore.get().getAllExams().stream()
            .flatMap(e -> DataStore.get().getAttemptsForExam(e.getId()).stream())
            .filter(a -> a.getStatus() == ExamAttempt.Status.SUBMITTED).count();

        cards.add(summaryCard("📚", "Total Exams",    String.valueOf(totalExams), UITheme.ACCENT));
        cards.add(summaryCard("✅", "Published",       String.valueOf(published), UITheme.ACCENT_GREEN));
        cards.add(summaryCard("👥", "Students",        String.valueOf(totalStudents), UITheme.ACCENT_WARN));
        cards.add(summaryCard("📝", "Attempts",        String.valueOf(totalAttempts), UITheme.TEXT_PRIMARY));

        // Attempts table
        String[] cols = {"Student", "Exam", "Score", "Percentage", "Result", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Exam exam : DataStore.get().getAllExams()) {
            for (ExamAttempt a : DataStore.get().getAttemptsForExam(exam.getId())) {
                if (a.getStatus() != ExamAttempt.Status.SUBMITTED) continue;
                String studentName = DataStore.get().getUserById(a.getStudentId())
                    .map(User::getName).orElse("Unknown");
                model.addRow(new Object[]{
                    studentName, exam.getTitle(),
                    a.getScore() + " / " + a.getTotalMarks(),
                    String.format("%.1f%%", a.getPercentage()),
                    a.isPassed() ? "PASS" : "FAIL",
                    a.getSubmittedAt() != null ? a.getSubmittedAt().toLocalDate() : "—"
                });
            }
        }

        JTable table = buildStyledTable(model);
        // Color result column
        table.getColumn("Result").setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setForeground("PASS".equals(v) ? UITheme.ACCENT_GREEN : UITheme.ACCENT_DANGER);
                setBackground(UITheme.BG_CARD);
                setFont(new Font("SansSerif", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 1, 8));
        sp.setBackground(UITheme.BG_CARD);
        sp.getViewport().setBackground(UITheme.BG_CARD);

        JLabel tableTitle = UITheme.heading("All Exam Attempts");
        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        panel.add(cards, BorderLayout.NORTH);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(tableTitle, BorderLayout.NORTH);
        bottom.add(sp, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private JPanel summaryCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundBorder(UITheme.BORDER, 1, 12),
            new EmptyBorder(18, 20, 18, 20)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel ico = new JLabel(icon + "  " + label);
        ico.setFont(UITheme.FONT_SMALL);
        ico.setForeground(UITheme.TEXT_MUTED);
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.BOLD, 30));
        val.setForeground(color);
        card.add(ico);
        card.add(Box.createVerticalStrut(4));
        card.add(val);
        return card;
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private void openCreateExamDialog() {
        JDialog dlg = new JDialog(this, "Create New Exam", true);
        dlg.setSize(480, 360);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_CARD);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JTextField titleF = UITheme.textField("Exam title");
        JTextField descF  = UITheme.textField("Description");
        JTextField durF   = UITheme.textField("Duration (minutes)");
        JTextField passF  = UITheme.textField("Passing score (%)");
        for (JComponent c : new JComponent[]{titleF, descF, durF, passF}) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        JButton create = UITheme.primaryButton("Create Exam");
        create.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        create.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel errLbl = UITheme.muted(" ");
        errLbl.setForeground(UITheme.ACCENT_DANGER);
        errLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        create.addActionListener(e -> {
            try {
                String t = titleF.getText().trim();
                if (t.isEmpty()) { errLbl.setText("Title is required"); return; }
                int dur  = Integer.parseInt(durF.getText().trim());
                int pass = Integer.parseInt(passF.getText().trim());
                Exam exam = new Exam(t, descF.getText().trim(), dur, pass, admin.getId());
                DataStore.get().addExam(exam);
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "Exam created! Now add questions.", "Created", JOptionPane.INFORMATION_MESSAGE);
                refreshAndReopen();
            } catch (NumberFormatException ex) {
                errLbl.setText("Duration and pass score must be numbers.");
            }
        });

        panel.add(UITheme.heading("Create New Exam"));
        panel.add(Box.createVerticalStrut(18));
        panel.add(lbl("Title")); panel.add(Box.createVerticalStrut(4)); panel.add(titleF);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lbl("Description")); panel.add(Box.createVerticalStrut(4)); panel.add(descF);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lbl("Duration (minutes)")); panel.add(Box.createVerticalStrut(4)); panel.add(durF);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lbl("Passing Score (%)")); panel.add(Box.createVerticalStrut(4)); panel.add(passF);
        panel.add(Box.createVerticalStrut(6)); panel.add(errLbl);
        panel.add(Box.createVerticalStrut(16)); panel.add(create);

        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    private void openQuestionsDialog(Exam exam) {
        new QuestionsDialog(this, exam).setVisible(true);
        // refresh
        refreshAndReopen();
    }

    private void refreshAndReopen() {
        dispose();
        new AdminDashboard().setVisible(true);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_INPUT);
                } else {
                    c.setBackground(new Color(UITheme.ACCENT.getRed(),
                        UITheme.ACCENT.getGreen(), UITheme.ACCENT.getBlue(), 50));
                }
                c.setForeground(UITheme.TEXT_PRIMARY);
                return c;
            }
        };
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.getTableHeader().setBackground(UITheme.BG_INPUT);
        table.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        table.setSelectionBackground(new Color(UITheme.ACCENT.getRed(),
            UITheme.ACCENT.getGreen(), UITheme.ACCENT.getBlue(), 50));
        return table;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // Renders text buttons in the actions column
    static class ActionButtonRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int row, int col) {
            JPanel p = new JPanel(new GridLayout(1, 3, 4, 0));
            p.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_INPUT);
            p.setBorder(new EmptyBorder(4, 6, 4, 6));

            Exam exam = row < DataStore.get().getAllExams().size() ?
                DataStore.get().getAllExams().get(row) : null;
            boolean pub = exam != null && exam.isPublished();

            for (String label : new String[]{"Questions", pub ? "Unpublish" : "Publish", "Delete"}) {
                JLabel btn = new JLabel(label, SwingConstants.CENTER);
                btn.setFont(new Font("SansSerif", Font.BOLD, 11));
                btn.setForeground(label.equals("Delete") ? UITheme.ACCENT_DANGER :
                                  label.equals("Questions") ? UITheme.ACCENT : UITheme.ACCENT_WARN);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new UITheme.RoundBorder(UITheme.BORDER, 1, 6),
                    new EmptyBorder(2, 4, 2, 4)
                ));
                btn.setOpaque(true);
                btn.setBackground(UITheme.BG_INPUT);
                p.add(btn);
            }
            return p;
        }
    }
}
