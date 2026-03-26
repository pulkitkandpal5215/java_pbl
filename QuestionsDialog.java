package com.examgui.ui;

import com.examgui.model.*;
import com.examgui.util.UITheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Dialog to manage questions for a specific exam.
 * Admin can view, add, and delete questions.
 */
public class QuestionsDialog extends JDialog {

    private final Exam exam;
    private DefaultTableModel tableModel;

    public QuestionsDialog(JFrame parent, Exam exam) {
        super(parent, "Questions — " + exam.getTitle(), true);
        this.exam = exam;
        setSize(860, 580);
        setLocationRelativeTo(parent);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 14, 0));
        JLabel title = UITheme.heading("Questions for: " + exam.getTitle());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton addBtn = UITheme.primaryButton("+ Add Question");
        addBtn.addActionListener(e -> openAddQuestionDialog());
        btnRow.add(addBtn);

        header.add(title, BorderLayout.WEST);
        header.add(btnRow, BorderLayout.EAST);

        // Table
        String[] cols = {"#", "Question", "A", "B", "C", "D", "Answer", "Marks"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshTable();

        JTable table = new JTable(tableModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                c.setBackground(row % 2 == 0 ? UITheme.BG_CARD : UITheme.BG_INPUT);
                c.setForeground(UITheme.TEXT_PRIMARY);
                if (col == 6) c.setForeground(UITheme.ACCENT_GREEN);
                return c;
            }
        };
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_SMALL);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.getTableHeader().setBackground(UITheme.BG_INPUT);
        table.getTableHeader().setForeground(UITheme.TEXT_MUTED);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));

        // Column widths
        table.getColumnModel().getColumn(0).setMaxWidth(35);
        table.getColumnModel().getColumn(6).setMaxWidth(70);
        table.getColumnModel().getColumn(7).setMaxWidth(60);

        // Delete on right-click
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete Question");
        deleteItem.setBackground(UITheme.BG_CARD);
        deleteItem.setForeground(UITheme.ACCENT_DANGER);
        deleteItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0 && row < exam.getQuestions().size()) {
                Question q = exam.getQuestions().get(row);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this question?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    exam.removeQuestion(q);
                    refreshTable();
                }
            }
        });
        popup.add(deleteItem);
        table.setComponentPopupMenu(popup);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new UITheme.RoundBorder(UITheme.BORDER, 1, 8));
        sp.setBackground(UITheme.BG_CARD);
        sp.getViewport().setBackground(UITheme.BG_CARD);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));
        JLabel info = UITheme.muted(exam.getQuestions().size() + " question(s)  ·  Total marks: " + exam.getTotalMarks() + "  ·  Right-click a row to delete");
        JButton closeBtn = UITheme.ghostButton("Close");
        closeBtn.addActionListener(e -> dispose());
        footer.add(info, BorderLayout.WEST);
        footer.add(closeBtn, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        return root;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int i = 1;
        for (Question q : exam.getQuestions()) {
            tableModel.addRow(new Object[]{
                i++, truncate(q.getText(), 50),
                truncate(q.getOptionA(), 20), truncate(q.getOptionB(), 20),
                truncate(q.getOptionC(), 20), truncate(q.getOptionD(), 20),
                q.getCorrectAnswer(), q.getMarks()
            });
        }
    }

    private void openAddQuestionDialog() {
        JDialog dlg = new JDialog(this, "Add Question", true);
        dlg.setSize(560, 560);
        dlg.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BG_CARD);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(22, 28, 22, 28));

        JTextField qText = UITheme.textField("Question text");
        JTextField optA  = UITheme.textField("Option A");
        JTextField optB  = UITheme.textField("Option B");
        JTextField optC  = UITheme.textField("Option C");
        JTextField optD  = UITheme.textField("Option D");
        JTextField marks = UITheme.textField("Marks (default: 1)");

        for (JComponent c : new JComponent[]{qText, optA, optB, optC, optD, marks}) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }

        String[] answerOpts = {"A", "B", "C", "D"};
        JComboBox<String> correctCombo = new JComboBox<>(answerOpts);
        correctCombo.setBackground(UITheme.BG_INPUT);
        correctCombo.setForeground(UITheme.TEXT_PRIMARY);
        correctCombo.setFont(UITheme.FONT_BODY);
        correctCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel errLbl = UITheme.muted(" ");
        errLbl.setForeground(UITheme.ACCENT_DANGER);
        errLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton saveBtn = UITheme.primaryButton("Add Question");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String qt = qText.getText().trim();
            String a = optA.getText().trim(), b = optB.getText().trim();
            String c = optC.getText().trim(), d = optD.getText().trim();
            if (qt.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
                errLbl.setText("All fields are required."); return;
            }
            int m = 1;
            try { if (!marks.getText().trim().isEmpty()) m = Integer.parseInt(marks.getText().trim()); }
            catch (NumberFormatException ex) { errLbl.setText("Marks must be a number."); return; }
            char correct = ((String) correctCombo.getSelectedItem()).charAt(0);
            exam.addQuestion(new Question(qt, a, b, c, d, correct, m));
            refreshTable();
            dlg.dispose();
        });

        panel.add(UITheme.heading("Add Question"));
        panel.add(Box.createVerticalStrut(16));
        panel.add(lbl("Question Text")); panel.add(Box.createVerticalStrut(4)); panel.add(qText);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lbl("Option A")); panel.add(Box.createVerticalStrut(4)); panel.add(optA);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lbl("Option B")); panel.add(Box.createVerticalStrut(4)); panel.add(optB);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lbl("Option C")); panel.add(Box.createVerticalStrut(4)); panel.add(optC);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lbl("Option D")); panel.add(Box.createVerticalStrut(4)); panel.add(optD);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lbl("Correct Answer")); panel.add(Box.createVerticalStrut(4)); panel.add(correctCombo);
        panel.add(Box.createVerticalStrut(8));
        panel.add(lbl("Marks")); panel.add(Box.createVerticalStrut(4)); panel.add(marks);
        panel.add(Box.createVerticalStrut(6)); panel.add(errLbl);
        panel.add(Box.createVerticalStrut(14)); panel.add(saveBtn);

        dlg.setContentPane(new JScrollPane(panel));
        dlg.setVisible(true);
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private String truncate(String s, int max) {
        return s != null && s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
