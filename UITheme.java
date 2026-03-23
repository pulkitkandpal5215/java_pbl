package com.examgui.util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * 🎨 UITheme — Global design system
 * Deep navy + electric teal + warm amber accent.
 * Clean, professional, modern.
 */
public class UITheme {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(0x0F1923);   // deep navy
    public static final Color BG_CARD       = new Color(0x162030);   // card bg
    public static final Color BG_INPUT      = new Color(0x1C2B3A);   // input bg
    public static final Color ACCENT        = new Color(0x00C9A7);   // teal
    public static final Color ACCENT_HOVER  = new Color(0x00B395);
    public static final Color ACCENT_WARN   = new Color(0xFFB347);   // amber
    public static final Color ACCENT_DANGER = new Color(0xFF5F5F);   // red
    public static final Color ACCENT_GREEN  = new Color(0x4CAF50);
    public static final Color TEXT_PRIMARY  = new Color(0xF0F4F8);
    public static final Color TEXT_MUTED    = new Color(0x7A8FA6);
    public static final Color BORDER        = new Color(0x243447);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static Font FONT_TITLE;
    public static Font FONT_HEADING;
    public static Font FONT_BODY;
    public static Font FONT_SMALL;
    public static Font FONT_MONO;

    static {
        // Use built-in fonts for portability
        FONT_TITLE   = new Font("SansSerif", Font.BOLD,   28);
        FONT_HEADING = new Font("SansSerif", Font.BOLD,   16);
        FONT_BODY    = new Font("SansSerif", Font.PLAIN,  14);
        FONT_SMALL   = new Font("SansSerif", Font.PLAIN,  12);
        FONT_MONO    = new Font("Monospaced", Font.BOLD,  13);
    }

    // ── Global UIManager defaults ─────────────────────────────────────────────
    public static void applyGlobalTheme() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("Panel.background",          BG_DARK);
        UIManager.put("Label.foreground",          TEXT_PRIMARY);
        UIManager.put("Label.font",                FONT_BODY);
        UIManager.put("Button.background",         ACCENT);
        UIManager.put("Button.foreground",         BG_DARK);
        UIManager.put("Button.font",               FONT_BODY);
        UIManager.put("ScrollPane.background",     BG_DARK);
        UIManager.put("ScrollBar.background",      BG_CARD);
        UIManager.put("ScrollBar.thumb",           BORDER);
        UIManager.put("OptionPane.background",     BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    // ── Component Factories ───────────────────────────────────────────────────

    /** A styled panel with card background */
    public static JPanel card(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(20, 24, 20, 24)
        ));
        return p;
    }

    /** Primary action button (teal) */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? ACCENT_HOVER :
                          getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BG_DARK);
                g2.setFont(FONT_HEADING);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(180, 42));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Danger button (red) */
    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.addPropertyChangeListener(e -> {});
        // Override paint to use danger color
        return styleButtonColor(btn, ACCENT_DANGER);
    }

    /** Warning button (amber) */
    public static JButton warningButton(String text) {
        return styleButtonColor(primaryButton(text), ACCENT_WARN);
    }

    /** Ghost button (outline) */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), 120));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.setColor(TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.setColor(getModel().isRollover() ? TEXT_PRIMARY : TEXT_MUTED);
                g2.setFont(FONT_BODY);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static JButton styleButtonColor(JButton original, Color color) {
        JButton btn = new JButton(original.getText()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed() || getModel().isRollover()
                    ? color.darker() : color;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_HEADING);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Styled text field */
    public static JTextField textField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_BODY);
                    g2.drawString(placeholder, 10, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 1);
                }
                g2.dispose();
            }
        };
        tf.setOpaque(false);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(ACCENT);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 1, 8),
            new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(280, 40));
        return tf;
    }

    /** Styled password field */
    public static JPasswordField passwordField(String placeholder) {
        JPasswordField pf = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_BODY);
                    g2.drawString(placeholder, 10, getHeight()/2 + g2.getFontMetrics().getAscent()/2 - 1);
                }
                g2.dispose();
            }
        };
        pf.setOpaque(false);
        pf.setBackground(BG_INPUT);
        pf.setForeground(TEXT_PRIMARY);
        pf.setCaretColor(ACCENT);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 1, 8),
            new EmptyBorder(6, 10, 6, 10)
        ));
        pf.setPreferredSize(new Dimension(280, 40));
        return pf;
    }

    /** Section label */
    public static JLabel heading(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Muted label */
    public static JLabel muted(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    /** Accent colored label */
    public static JLabel accentLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(ACCENT);
        return lbl;
    }

    /** Horizontal separator */
    public static JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setBackground(BORDER);
        return sep;
    }

    // Custom round border
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int thickness, radius;
        public RoundBorder(Color color, int thickness, int radius) {
            this.color = color; this.thickness = thickness; this.radius = radius;
        }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness+2, thickness+2, thickness+2, thickness+2); }
    }
}
