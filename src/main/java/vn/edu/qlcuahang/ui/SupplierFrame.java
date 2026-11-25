package vn.edu.qlcuahang.ui;

import vn.edu.qlcuahang.config.Db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Form CRUD và tìm kiếm nhà cung cấp. */
public class SupplierFrame extends JFrame {
    private final JTextField txtSearch = Ui.textField();
    private final JTextField txtName = Ui.textField();
    private final JTextField txtPhone = Ui.textField();
    private final JTextField txtEmail = Ui.textField();
    private final JTextField txtAddress = Ui.textField();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Mã NCC", "Tên nhà cung cấp", "Số điện thoại", "Email", "Địa chỉ"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedId;

    public SupplierFrame() {
        setTitle("Quản lý nhà cung cấp");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(850, 620));
        setSize(1100, 740);
        setLocationRelativeTo(null);
        buildUi();
        loadSuppliers("");
    }

    private void buildUi() {
        JPanel root = Ui.pagePanel(); setContentPane(root);
        root.add(Ui.header("Quản lý nhà cung cấp", "Quản lý các đơn vị cung ứng sản phẩm cho cửa hàng."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14)); body.setOpaque(false);

        JPanel form = Ui.whiteCard();
        JLabel title = new JLabel("THÔNG TIN NHÀ CUNG CẤP"); title.setForeground(Ui.NAVY); title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        form.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 2, 12, 9)); fields.setOpaque(false);
        fields.add(fieldBox("Tên nhà cung cấp *", txtName)); fields.add(fieldBox("Số điện thoại", txtPhone));
        fields.add(fieldBox("Email", txtEmail)); fields.add(fieldBox("Địa chỉ", txtAddress));
        form.add(fields, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2)); actions.setOpaque(false);
        JButton add = Ui.button("＋ Thêm mới", Ui.GREEN); JButton update = Ui.button("✎ Cập nhật", Ui.BLUE);
        JButton delete = Ui.button("⌫ Xóa", Ui.RED); JButton clear = Ui.button("Làm mới form", new Color(104, 117, 136));
        add.addActionListener(e -> addSupplier()); update.addActionListener(e -> updateSupplier());
        delete.addActionListener(e -> deleteSupplier()); clear.addActionListener(e -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear); form.add(actions, BorderLayout.SOUTH);
        body.add(form, BorderLayout.NORTH);

        JPanel list = Ui.whiteCard();
        JPanel searchBar = new JPanel(new BorderLayout(10, 0)); searchBar.setOpaque(false);
        JLabel listTitle = new JLabel("DANH SÁCH NHÀ CUNG CẤP"); listTitle.setForeground(Ui.NAVY); listTitle.setFont(listTitle.getFont().deriveFont(Font.BOLD, 13f));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); controls.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(255, 34)); txtSearch.setToolTipText("Tìm theo tên, số điện thoại hoặc email");
        JButton search = Ui.button("Tìm kiếm", Ui.BLUE); JButton reload = Ui.button("Tải lại", new Color(104, 117, 136));
        search.addActionListener(e -> loadSuppliers(txtSearch.getText().trim())); txtSearch.addActionListener(e -> loadSuppliers(txtSearch.getText().trim()));
        reload.addActionListener(e -> { txtSearch.setText(""); loadSuppliers(""); });
        controls.add(txtSearch); controls.add(search); controls.add(reload); searchBar.add(listTitle, BorderLayout.WEST); searchBar.add(controls, BorderLayout.EAST);
        list.add(searchBar, BorderLayout.NORTH);
        Ui.styleTable(table); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFromSelectedRow(); });
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel tip = new JLabel("Chọn một dòng để sửa hoặc xóa. Nhà cung cấp có phiếu nhập sẽ không thể xóa.");
        tip.setForeground(Ui.MUTED); tip.setFont(tip.getFont().deriveFont(11.5f)); list.add(tip, BorderLayout.SOUTH);
        body.add(list, BorderLayout.CENTER); root.add(body, BorderLayout.CENTER);
    }

    private JPanel fieldBox(String label, Component field) {
        JPanel box = new JPanel(new BorderLayout(0, 5)); box.setOpaque(false);
        box.add(Ui.fieldLabel(label), BorderLayout.NORTH); box.add(field, BorderLayout.CENTER); return box;
    }

    private void loadSuppliers(String keyword) {
        String sql = "SELECT MaNCC, TenNCC, ISNULL(SoDienThoai,''), ISNULL(Email,''), ISNULL(DiaChi,'') FROM dbo.NhaCungCap "
                + "WHERE TenNCC LIKE ? OR ISNULL(SoDienThoai,'') LIKE ? OR ISNULL(Email,'') LIKE ? ORDER BY MaNCC DESC";
        String like = "%" + keyword + "%"; model.setRowCount(0);
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private SupplierInput validateForm() {
        String name = txtName.getText().trim(), phone = txtPhone.getText().trim(), email = txtEmail.getText().trim();
        if (name.isEmpty()) { Ui.showError(this, "Tên nhà cung cấp là bắt buộc."); return null; }
        if (!phone.isEmpty() && !phone.matches("[0-9+() .-]{8,20}")) { Ui.showError(this, "Số điện thoại chưa hợp lệ."); return null; }
        if (!email.isEmpty() && !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) { Ui.showError(this, "Địa chỉ email chưa hợp lệ."); return null; }
        return new SupplierInput(name, blankToNull(phone), blankToNull(email), blankToNull(txtAddress.getText()));
    }

    private void addSupplier() {
        SupplierInput in = validateForm(); if (in == null) return;
        String sql = "INSERT INTO dbo.NhaCungCap (TenNCC, SoDienThoai, Email, DiaChi) VALUES (?, ?, ?, ?)";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, in); ps.executeUpdate(); Ui.showSuccess(this, "Đã thêm nhà cung cấp."); clearForm(); loadSuppliers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void updateSupplier() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn nhà cung cấp trước khi cập nhật."); return; }
        SupplierInput in = validateForm(); if (in == null) return;
        String sql = "UPDATE dbo.NhaCungCap SET TenNCC=?, SoDienThoai=?, Email=?, DiaChi=? WHERE MaNCC=?";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, in); ps.setInt(5, selectedId); ps.executeUpdate(); Ui.showSuccess(this, "Đã cập nhật nhà cung cấp."); clearForm(); loadSuppliers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void deleteSupplier() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn nhà cung cấp trước khi xóa."); return; }
        if (!Ui.confirmDelete(this, "nhà cung cấp mã " + selectedId)) return;
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM dbo.NhaCungCap WHERE MaNCC=?")) {
            ps.setInt(1, selectedId); ps.executeUpdate(); Ui.showSuccess(this, "Đã xóa nhà cung cấp."); clearForm(); loadSuppliers(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void bind(PreparedStatement ps, SupplierInput in) throws SQLException {
        ps.setNString(1, in.name);
        if (in.phone == null) ps.setNull(2, java.sql.Types.VARCHAR); else ps.setString(2, in.phone);
        if (in.email == null) ps.setNull(3, java.sql.Types.VARCHAR); else ps.setString(3, in.email);
        if (in.address == null) ps.setNull(4, java.sql.Types.NVARCHAR); else ps.setNString(4, in.address);
    }

    private void fillFromSelectedRow() {
        int view = table.getSelectedRow(); if (view < 0) return; int row = table.convertRowIndexToModel(view);
        selectedId = ((Number) model.getValueAt(row, 0)).intValue(); txtName.setText(String.valueOf(model.getValueAt(row, 1)));
        txtPhone.setText(String.valueOf(model.getValueAt(row, 2))); txtEmail.setText(String.valueOf(model.getValueAt(row, 3)));
        txtAddress.setText(String.valueOf(model.getValueAt(row, 4)));
    }

    private void clearForm() {
        selectedId = null; table.clearSelection(); txtName.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtAddress.setText(""); txtName.requestFocusInWindow();
    }
    private String blankToNull(String text) { String value = text == null ? "" : text.trim(); return value.isEmpty() ? null : value; }
    private record SupplierInput(String name, String phone, String email, String address) { }
}
