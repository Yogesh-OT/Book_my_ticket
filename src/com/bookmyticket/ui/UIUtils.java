package com.bookmyticket.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Common UI constants, styling helpers, and color palette.
 * Provides a modern, clean, and cohesive look across all Swing frames.
 */
public class UIUtils {
    // Color Palette
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);      // Vibrant Blue
    public static final Color COLOR_PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color COLOR_SECONDARY = new Color(100, 116, 139);   // Slate Gray
    public static final Color COLOR_BG = new Color(248, 250, 252);          // Light clean background
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);      // Dark slate
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);
    public static final Color COLOR_ACCENT = new Color(16, 185, 129);       // Emerald Green
    public static final Color COLOR_DANGER = new Color(239, 68, 68);        // Crimson Red
    public static final Color COLOR_WARNING = new Color(245, 158, 11);      // Amber

    // Seat Colors
    public static final Color COLOR_SEAT_REGULAR = new Color(59, 130, 246);   // Blue
    public static final Color COLOR_SEAT_PREMIUM = new Color(139, 92, 246);  // Purple
    public static final Color COLOR_SEAT_SELECTED = new Color(16, 185, 129); // Green
    public static final Color COLOR_SEAT_BOOKED = new Color(148, 163, 184);  // Slate Gray disabled

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1, true),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(226, 232, 240), COLOR_TEXT_MAIN);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, COLOR_DANGER, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, COLOR_ACCENT, Color.WHITE);
    }

    public static JPanel createHeader(String titleText, String subtitleText) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42)); // Deep Navy
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);

        if (subtitleText != null && !subtitleText.isEmpty()) {
            JLabel sub = new JLabel(subtitleText);
            sub.setFont(FONT_REGULAR);
            sub.setForeground(new Color(148, 163, 184));
            header.add(sub, BorderLayout.EAST);
        }
        return header;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(28);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(COLOR_TEXT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_TEXT_MAIN);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }
}
