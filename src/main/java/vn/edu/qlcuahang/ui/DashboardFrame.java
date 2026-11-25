package vn.edu.qlcuahang.ui;

import vn.edu.qlcuahang.config.Db;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

/** Màn hình chính, điều hướng đến các form quản lý dữ liệu. */
public class DashboardFrame extends JFrame {
    private final JLabel connectionStatus = new JLabel("Chưa kiểm tra kết nối");

    public DashboardFrame() {
        setTitle("PhoneStore | Quản lý cửa hàng điện thoại");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 610));
        setSize(1080, 740);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Ui.BG);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(Ui.BG);
        root.setBorder(new EmptyBorder(24, 28, 20, 28));
        setContentPane(root);

        JPanel hero = new JPanel(new BorderLayout(18, 10));
        hero.setBackground(Ui.NAVY);
        hero.setBorder(new EmptyBorder(24, 26, 24, 26));
        JLabel title = new JLabel("PHONE STORE MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Phần mềm quản lý cửa hàng điện thoại · Java Swing + SQL Server");
        subtitle.setForeground(new Color(214, 225, 241));
        subtitle.setFont(subtitle.getFont().deriveFont(13.5f));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(title);
        titles.add(Box.createVerticalStrut(8));
        titles.add(subtitle);
        hero.add(titles, BorderLayout.CENTER);
        JButton testConnection = Ui.button("Kiểm tra CSDL", Ui.GREEN);
        testConnection.addActionListener(e -> testConnection());
        hero.add(testConnection, BorderLayout.EAST);
        root.add(hero, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        JLabel sectionTitle = new JLabel("KHU VỰC QUẢN LÝ");
        sectionTitle.setForeground(Ui.NAVY);
        sectionTitle.setFont(sectionTitle.getFont().deriveFont(Font.BOLD, 15f));
        center.add(sectionTitle, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setOpaque(false);
        cards.add(createCard("01", "Quản lý sản phẩm", "Thêm, sửa, xóa và tìm kiếm điện thoại; quản lý giá bán và tồn kho.",
                "Mở quản lý sản phẩm", () -> new ProductFrame().setVisible(true)));
        cards.add(createCard("02", "Quản lý khách hàng", "Lưu thông tin, số điện thoại, email và địa chỉ khách hàng.",
                "Mở quản lý khách hàng", () -> new CustomerFrame().setVisible(true)));
        cards.add(createCard("03", "Quản lý nhà cung cấp", "Theo dõi thông tin các nhà cung cấp của cửa hàng.",
                "Mở quản lý nhà cung cấp", () -> new SupplierFrame().setVisible(true)));
        cards.add(createCard("04", "Quản lý danh mục", "Tạo và chỉnh sửa nhóm sản phẩm để phân loại điện thoại, phụ kiện.",
                "Mở quản lý danh mục", () -> new CategoryFrame().setVisible(true)));
        center.add(cards, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        connectionStatus.setForeground(Ui.MUTED);
        connectionStatus.setFont(connectionStatus.getFont().deriveFont(12f));
        footer.add(connectionStatus, BorderLayout.WEST);
        JLabel note = new JLabel("Đồ án Java · Dữ liệu được lưu trong SQL Server");
        note.setForeground(Ui.MUTED);
        footer.add(note, BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createCard(String number, String title, String description, String buttonText, Runnable action) {
        JPanel card = Ui.whiteCard();
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new LineBorder(Ui.BORDER, 1, true), new EmptyBorder(18, 18, 18, 18)));
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        JLabel badge = new JLabel(number, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(new Color(228, 237, 252));
        badge.setForeground(Ui.BLUE);
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 14f));
        badge.setPreferredSize(new Dimension(46, 42));
        JLabel heading = new JLabel(title);
        heading.setForeground(Ui.NAVY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        top.add(badge, BorderLayout.WEST);
        top.add(heading, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH);

        JLabel desc = new JLabel("<html><div style='width:310px'>" + description + "</div></html>");
        desc.setForeground(Ui.MUTED);
        desc.setVerticalAlignment(SwingConstants.TOP);
        card.add(desc, BorderLayout.CENTER);
        JButton open = Ui.button(buttonText, Ui.BLUE);
        open.addActionListener(e -> action.run());
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(open);
        card.add(actionPanel, BorderLayout.SOUTH);
        return card;
    }

    private void testConnection() {
        connectionStatus.setText("Đang kiểm tra...");
        try (Connection ignored = Db.getConnection()) {
            connectionStatus.setForeground(Ui.GREEN);
            connectionStatus.setText("● Đã kết nối SQL Server thành công");
            Ui.showSuccess(this, "Kết nối đến SQL Server thành công! Có thể sử dụng các form quản lý.");
        } catch (SQLException ex) {
            connectionStatus.setForeground(Ui.RED);
            connectionStatus.setText("● Kết nối thất bại — kiểm tra config/db.properties");
            JOptionPane.showMessageDialog(this,
                    "Không kết nối được cơ sở dữ liệu.\n\n" + ex.getMessage()
                            + "\n\nXem README.md để cấu hình SQL Server.",
                    "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }
}
