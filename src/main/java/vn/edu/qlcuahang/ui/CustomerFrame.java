package vn.edu.qlcuahang.ui;

import vn.edu.qlcuahang.config.Db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Form CRUD và tìm kiếm khách hàng. */
public class CustomerFrame extends JFrame {
    private final JTextField txtSearch = Ui.textField();
    private final JTextField txtName = Ui.textField();
    private final JTextField txtPhone = Ui.textField();
    private final JTextField txtEmail = Ui.textField();
    private final JTextField txtAddress = Ui.textField();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã KH", "Họ tên", "Số điện thoại", "Email", "Địa chỉ", "Ngày tạo"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedId;

    public CustomerFrame() {
        setTitle("Quản lý khách hàng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(850, 620));
        setSize(1100, 740);
        setLocationRelativeTo(null);
        buildUi();
        loadCustomers("");
    }

    private void buildUi() {
        JPanel root = Ui.pagePanel();
        setContentPane(root);
        root.add(Ui.header("Quản lý khách hàng", "Thông tin khách hàng được lưu trực tiếp trong cơ sở dữ liệu SQL Server."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);

        JPanel form = Ui.whiteCard();
        JLabel title = new JLabel("THÔNG TIN KHÁCH HÀNG");
        title.setForeground(Ui.NAVY); title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        form.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 2, 12, 9));
        fields.setOpaque(false);
        fields.add(fieldBox("Họ và tên *", txtName));
        fields.add(fieldBox("Số điện thoại *", txtPhone));
        fields.add(fieldBox("Email", txtEmail));
        fields.add(fieldBox("Địa chỉ", txtAddress));
        form.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        actions.setOpaque(false);
        JButton add = Ui.button("＋ Thêm mới", Ui.GREEN);
        JButton update = Ui.button("✎ Cập nhật", Ui.BLUE);
        JButton delete = Ui.button("⌫ Xóa", Ui.RED);
        JButton clear = Ui.button("Làm mới form", new Color(104, 117, 136));
        add.addActionListener(e -> addCustomer());
        update.addActionListener(e -> updateCustomer());
        delete.addActionListener(e -> deleteCustomer());
        clear.addActionListener(e -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        form.add(actions, BorderLayout.SOUTH);
        body.add(form, BorderLayout.NORTH);

        JPanel list = Ui.whiteCard();
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel listTitle = new JLabel("DANH SÁCH KHÁCH HÀNG");
        listTitle.setForeground(Ui.NAVY); listTitle.setFont(listTitle.getFont().deriveFont(Font.BOLD, 13f));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(255, 34));
        txtSearch.setToolTipText("Tìm theo họ tên, số điện thoại hoặc email");
        JButton search = Ui.button("Tìm kiếm", Ui.BLUE);
        JButton reload = Ui.button("Tải lại", new Color(104, 117, 136));
        search.addActionListener(e -> loadCustomers(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadCustomers(txtSearch.getText().trim()));
        reload.addActionListener(e -> { txtSearch.setText(""); loadCustomers(""); });
        controls.add(txtSearch); controls.add(search); controls.add(reload);
        searchBar.add(listTitle, BorderLayout.WEST); searchBar.add(controls, BorderLayout.EAST);
        list.add(searchBar, BorderLayout.NORTH);

        Ui.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFromSelectedRow();
        });
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel tip = new JLabel("Chọn một dòng để sửa hoặc xóa. Khách hàng đã có hóa đơn sẽ không thể xóa.");
        tip.setForeground(Ui.MUTED); tip.setFont(tip.getFont().deriveFont(11.5f));
        list.add(tip, BorderLayout.SOUTH);
        body.add(list, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    private JPanel fieldBox(String label, Component field) {
        JPanel box = new JPanel(new BorderLayout(0, 5)); box.setOpaque(false);
        box.add(Ui.fieldLabel(label), BorderLayout.NORTH); box.add(field, BorderLayout.CENTER); return box;
    }

    private void loadCustomers(String keyword) {
        String sql = "SELECT MaKH, HoTen, SoDienThoai, ISNULL(Email,''), ISNULL(DiaChi,''), NgayTao "
                + "FROM dbo.KhachHang WHERE HoTen LIKE ? OR SoDienThoai LIKE ? OR ISNULL(Email,'') LIKE ? ORDER BY MaKH DESC";
        String like = "%" + keyword + "%";
        model.setRowCount(0);
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)});
            }
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private CustomerInput validateForm() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        if (name.isEmpty() || phone.isEmpty()) { Ui.showError(this, "Họ tên và số điện thoại là bắt buộc."); return null; }
        if (!phone.matches("[0-9+() .-]{8,20}")) { Ui.showError(this, "Số điện thoại chưa hợp lệ."); return null; }
        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) { Ui.showError(this, "Địa chỉ email chưa hợp lệ."); return null; }
        return new CustomerInput(name, phone, blankToNull(email), blankToNull(txtAddress.getText()));
    }

    private void addCustomer() {
        CustomerInput in = validateForm(); if (in == null) return;
        String sql = "INSERT INTO dbo.KhachHang (HoTen, SoDienThoai, Email, DiaChi) VALUES (?, ?, ?, ?)";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, in); ps.executeUpdate();
            Ui.showSuccess(this, "Đã thêm khách hàng thành công."); clearForm(); loadCustomers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void updateCustomer() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn khách hàng trong bảng trước khi cập nhật."); return; }
        CustomerInput in = validateForm(); if (in == null) return;
        String sql = "UPDATE dbo.KhachHang SET HoTen=?, SoDienThoai=?, Email=?, DiaChi=? WHERE MaKH=?";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, in); ps.setInt(5, selectedId); ps.executeUpdate();
            Ui.showSuccess(this, "Đã cập nhật khách hàng."); clearForm(); loadCustomers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void deleteCustomer() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn khách hàng trong bảng trước khi xóa."); return; }
        if (!Ui.confirmDelete(this, "khách hàng mã " + selectedId)) return;
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM dbo.KhachHang WHERE MaKH=?")) {
            ps.setInt(1, selectedId); ps.executeUpdate();
            Ui.showSuccess(this, "Đã xóa khách hàng."); clearForm(); loadCustomers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void bind(PreparedStatement ps, CustomerInput in) throws SQLException {
        ps.setNString(1, in.name); ps.setString(2, in.phone);
        if (in.email == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, in.email);
        if (in.address == null) ps.setNull(4, java.sql.Types.NVARCHAR); else ps.setNString(4, in.address);
    }

    private void fillFromSelectedRow() {
        int viewRow = table.getSelectedRow(); if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        selectedId = ((Number) model.getValueAt(row, 0)).intValue();
        txtName.setText(String.valueOf(model.getValueAt(row, 1)));
        txtPhone.setText(String.valueOf(model.getValueAt(row, 2)));
        txtEmail.setText(String.valueOf(model.getValueAt(row, 3)));
        txtAddress.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void clearForm() {
        selectedId = null; table.clearSelection(); txtName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtAddress.setText(""); txtName.requestFocusInWindow();
    }

    private String blankToNull(String text) { String value = text == null ? "" : text.trim(); return value.isEmpty() ? null : value; }
    private record CustomerInput(String name, String phone, String email, String address) { }
}
