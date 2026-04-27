package com.examgui.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * JavaFX Design System — deep navy + electric teal.
 */
public class UITheme {

    // ── Palette (CSS hex strings) ─────────────────────────────────────────────
    public static final String BG_DARK       = "#0F1923";
    public static final String BG_CARD       = "#162030";
    public static final String BG_INPUT      = "#1C2B3A";
    public static final String ACCENT        = "#00C9A7";
    public static final String ACCENT_HOVER  = "#00B395";
    public static final String ACCENT_WARN   = "#FFB347";
    public static final String ACCENT_DANGER = "#FF5F5F";
    public static final String ACCENT_GREEN  = "#4CAF50";
    public static final String TEXT_PRIMARY  = "#F0F4F8";
    public static final String TEXT_MUTED    = "#7A8FA6";
    public static final String BORDER        = "#243447";

    // ── JavaFX Color objects ──────────────────────────────────────────────────
    public static final Color C_BG_DARK       = Color.web(BG_DARK);
    public static final Color C_BG_CARD       = Color.web(BG_CARD);
    public static final Color C_BG_INPUT      = Color.web(BG_INPUT);
    public static final Color C_ACCENT        = Color.web(ACCENT);
    public static final Color C_ACCENT_WARN   = Color.web(ACCENT_WARN);
    public static final Color C_ACCENT_DANGER = Color.web(ACCENT_DANGER);
    public static final Color C_ACCENT_GREEN  = Color.web(ACCENT_GREEN);
    public static final Color C_TEXT_PRIMARY  = Color.web(TEXT_PRIMARY);
    public static final Color C_TEXT_MUTED    = Color.web(TEXT_MUTED);
    public static final Color C_BORDER        = Color.web(BORDER);

    // ── Global stylesheet (injected into Scene) ───────────────────────────────
    public static final String GLOBAL_CSS = """
        .root {
            -fx-background-color: #0F1923;
            -fx-font-family: 'SansSerif';
        }
        .scroll-pane {
            -fx-background-color: transparent;
            -fx-border-color: transparent;
        }
        .scroll-pane .viewport {
            -fx-background-color: transparent;
        }
        .scroll-pane .scroll-bar:vertical .track { -fx-background-color: #162030; }
        .scroll-pane .scroll-bar:vertical .thumb { -fx-background-color: #243447; -fx-background-radius: 4; }
        .scroll-pane .scroll-bar:horizontal { -fx-pref-height: 0; }
        .tab-pane .tab-header-area { -fx-background-color: #162030; }
        .tab-pane .tab { -fx-background-color: #162030; -fx-padding: 8 16 8 16; }
        .tab-pane .tab:selected { -fx-background-color: #0F1923; }
        .tab-pane .tab .tab-label { -fx-text-fill: #7A8FA6; -fx-font-size: 13px; }
        .tab-pane .tab:selected .tab-label { -fx-text-fill: #00C9A7; }
        .tab-pane .tab-header-background { -fx-background-color: #162030; }
        .tab-pane .tab:selected .tab-container { -fx-border-color: transparent transparent #00C9A7 transparent; -fx-border-width: 0 0 2 0; }
        .tab-pane .tab-content-area { -fx-background-color: #0F1923; }
        .table-view { -fx-background-color: #162030; -fx-border-color: #243447; }
        .table-view .column-header-background { -fx-background-color: #1C2B3A; }
        .table-view .column-header { -fx-background-color: transparent; }
        .table-view .column-header .label { -fx-text-fill: #7A8FA6; -fx-font-weight: bold; }
        .table-view .table-row-cell { -fx-background-color: #162030; -fx-border-color: transparent; }
        .table-view .table-row-cell:odd { -fx-background-color: #1C2B3A; }
        .table-view .table-row-cell:selected { -fx-background-color: rgba(0,201,167,0.15); }
        .table-view .table-cell { -fx-text-fill: #F0F4F8; -fx-border-color: transparent; }
        .table-view .filler { -fx-background-color: #1C2B3A; }
        .combo-box { -fx-background-color: #1C2B3A; -fx-border-color: #243447; -fx-border-radius: 6; -fx-background-radius: 6; }
        .combo-box .list-cell { -fx-text-fill: #F0F4F8; -fx-background-color: transparent; }
        .combo-box-popup .list-view { -fx-background-color: #1C2B3A; }
        .combo-box-popup .list-cell { -fx-text-fill: #F0F4F8; -fx-background-color: #1C2B3A; }
        .combo-box-popup .list-cell:selected { -fx-background-color: #243447; }
        .combo-box .arrow-button { -fx-background-color: transparent; }
        .combo-box .arrow { -fx-background-color: #7A8FA6; }
        .separator .line { -fx-border-color: #243447; -fx-border-width: 1 0 0 0; }
        .context-menu { -fx-background-color: #162030; -fx-border-color: #243447; }
        .menu-item { -fx-background-color: transparent; }
        .menu-item .label { -fx-text-fill: #F0F4F8; }
        .menu-item:focused { -fx-background-color: #1C2B3A; }
        .dialog-pane { -fx-background-color: #162030; }
        .dialog-pane .content { -fx-background-color: #162030; }
        .dialog-pane .header-panel { -fx-background-color: #0F1923; }
        .dialog-pane .header-panel .label { -fx-text-fill: #F0F4F8; }
        .dialog-pane .button-bar .button { -fx-background-color: #00C9A7; -fx-text-fill: #0F1923; -fx-font-weight: bold; -fx-background-radius: 6; }
        """;

    // ── Button factories ──────────────────────────────────────────────────────

    public static Button primaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
            -fx-background-color: #00C9A7;
            -fx-text-fill: #0F1923;
            -fx-font-weight: bold;
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-padding: 10 22 10 22;
            """);
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle()
            .replace("#00C9A7", "#00B395")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
            .replace("#00B395", "#00C9A7")));
        return btn;
    }

    public static Button dangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
            -fx-background-color: #FF5F5F;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-padding: 10 22 10 22;
            """);
        return btn;
    }

    public static Button warningButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
            -fx-background-color: #FFB347;
            -fx-text-fill: #0F1923;
            -fx-font-weight: bold;
            -fx-font-size: 13px;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-padding: 10 22 10 22;
            """);
        return btn;
    }

    public static Button ghostButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-border-color: #243447;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-text-fill: #7A8FA6;
            -fx-font-size: 13px;
            -fx-cursor: hand;
            -fx-padding: 8 18 8 18;
            """);
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle()
            .replace("#7A8FA6", "#F0F4F8")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle()
            .replace("#F0F4F8", "#7A8FA6")));
        return btn;
    }

    // ── Input factories ───────────────────────────────────────────────────────

    public static TextField textField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("""
            -fx-background-color: #1C2B3A;
            -fx-border-color: #243447;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-text-fill: #F0F4F8;
            -fx-prompt-text-fill: #7A8FA6;
            -fx-font-size: 13px;
            -fx-padding: 10 12 10 12;
            """);
        tf.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                tf.setStyle(tf.getStyle().replace("#243447", "#00C9A7"));
            } else {
                tf.setStyle(tf.getStyle().replace("#00C9A7", "#243447"));
            }
        });
        return tf;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle("""
            -fx-background-color: #1C2B3A;
            -fx-border-color: #243447;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-text-fill: #F0F4F8;
            -fx-prompt-text-fill: #7A8FA6;
            -fx-font-size: 13px;
            -fx-padding: 10 12 10 12;
            """);
        pf.focusedProperty().addListener((obs, old, focused) -> {
            if (focused) {
                pf.setStyle(pf.getStyle().replace("#243447", "#00C9A7"));
            } else {
                pf.setStyle(pf.getStyle().replace("#00C9A7", "#243447"));
            }
        });
        return pf;
    }

    // ── Label factories ───────────────────────────────────────────────────────

    public static Label heading(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", FontWeight.BOLD, 16));
        l.setTextFill(C_TEXT_PRIMARY);
        return l;
    }

    public static Label title(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));
        l.setTextFill(C_ACCENT);
        return l;
    }

    public static Label muted(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", 12));
        l.setTextFill(C_TEXT_MUTED);
        return l;
    }

    public static Label body(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", 14));
        l.setTextFill(C_TEXT_PRIMARY);
        return l;
    }

    // ── Container helpers ─────────────────────────────────────────────────────

    /** Rounded card pane */
    public static VBox card() {
        VBox box = new VBox(12);
        box.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: #243447;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            """);
        box.setPadding(new Insets(20, 24, 20, 24));
        return box;
    }

    /** Dark page background */
    public static VBox page() {
        VBox box = new VBox();
        box.setStyle("-fx-background-color: #0F1923;");
        return box;
    }

    /** Nav bar */
    public static HBox navBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 24, 12, 24));
        bar.setStyle("""
            -fx-background-color: #162030;
            -fx-border-color: transparent transparent #243447 transparent;
            -fx-border-width: 0 0 1 0;
            """);
        return bar;
    }

    /** Styled separator */
    public static Separator separator() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #243447;");
        return sep;
    }

    /** Chip/badge label */
    public static Label chip(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", 11));
        l.setTextFill(C_TEXT_MUTED);
        l.setStyle("""
            -fx-background-color: #1C2B3A;
            -fx-background-radius: 20;
            -fx-padding: 3 10 3 10;
            """);
        return l;
    }

    /** Small field label above an input */
    public static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("SansSerif", 12));
        l.setTextFill(C_TEXT_MUTED);
        return l;
    }

    /** Error label */
    public static Label errorLabel() {
        Label l = new Label(" ");
        l.setFont(Font.font("SansSerif", 12));
        l.setTextFill(C_ACCENT_DANGER);
        return l;
    }
}
