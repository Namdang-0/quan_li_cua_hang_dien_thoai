package vn.edu.qlcuahang.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;

/** Các hàm giao diện dùng chung cho những màn hình quản lý. */
public final class Ui {
    public static final Color NAVY = new Color(28, 48, 82);
    public static final Color BLUE = new Color(43, 104, 196);
    public static final Color GREEN = new Color(35, 139, 91);
    public static final Color RED = new Color(190, 58, 58);
    public static final Color BG = new Color(244, 247, 251);
    public static final Color TEXT = new Color(38, 50, 68);
    public static final Color MUTED = new Color(103, 116, 135);
    public static final Color BORDER = new Color(220, 227, 237);

    private Ui() { }

    public static JPanel pagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 22, 20, 22));
        return panel;
    }

    public static JPanel header(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 23f));
        heading.setForeground(NAVY);
        JLabel sub = new JLabel(subtitle);
        sub.setFont(sub.getFont().deriveFont(13f));
        sub.setForeground(MUTED);
        sub.setBorder(new EmptyBorder(5, 0, 0, 0));
        panel.add(heading);
        panel.add(sub);
        return panel;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(160, 34));
        field.setMinimumSize(new Dimension(80, 32));
        return field;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12.5f));
        return label;
    }

    public static JButton button(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 12.5f));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 15, 9, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    public static JPanel whiteCard() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(29);
        table.setFont(table.getFont().deriveFont(12.5f));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(218, 233, 255));
        table.setSelectionForeground(NAVY);
        table.setGridColor(new Color(235, 239, 245));
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(header.getFont().deriveFont(Font.BOLD, 12.5f));
        header.setBackground(new Color(233, 239, 248));
        header.setForeground(NAVY);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
    }

    public static boolean confirmDelete(Component parent, String item) {
        return JOptionPane.showConfirmDialog(parent,
                "Bạn có chắc chắn muốn xóa " + item + " không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void showSqlError(Component parent, SQLException ex) {
        int code = ex.getErrorCode();
        String message;
        if (code == 2601 || code == 2627) {
            message = "Dữ liệu bị trùng với bản ghi đã tồn tại (ví dụ số điện thoại hoặc tên danh mục).";
        } else if (code == 547) {
            message = "Không thể thực hiện thao tác vì dữ liệu đang được bảng khác tham chiếu, "
                    + "hoặc dữ liệu vi phạm ràng buộc. Hãy kiểm tra khóa ngoại và các giá trị nhập.";
        } else if (code == 515) {
            message = "Thiếu dữ liệu bắt buộc. Hãy nhập đầy đủ các trường được yêu cầu.";
        } else {
            message = "Không thể thao tác với SQL Server. Kiểm tra kết nối và dữ liệu nhập.\n\nChi tiết: " + ex.getMessage();
        }
        JOptionPane.showMessageDialog(parent, message, "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông tin chưa hợp lệ", JOptionPane.WARNING_MESSAGE);
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
