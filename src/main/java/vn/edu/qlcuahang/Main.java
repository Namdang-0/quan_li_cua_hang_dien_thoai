package vn.edu.qlcuahang;

import vn.edu.qlcuahang.ui.DashboardFrame;

import javax.swing.*;
import java.awt.*;

/** Điểm bắt đầu của ứng dụng. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // FlatLaf được nạp từ dependency Maven; nếu thiếu thư viện thì dùng giao diện hệ thống.
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignoredAgain) {
                    // Giữ giao diện mặc định của Java Swing.
                }
            }
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Component.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            new DashboardFrame().setVisible(true);
        });
    }
}
