package vn.edu.qlcuahang.ui;

import vn.edu.qlcuahang.config.Db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Form CRUD và tìm kiếm danh mục sản phẩm. */
public class CategoryFrame extends JFrame {
    private final JTextField txtSearch = Ui.textField();
    private final JTextField txtName = Ui.textField();
    private final JTextField txtDescription = Ui.textField();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Mã danh mục", "Tên danh mục", "Mô tả"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private Integer selectedId;

    public CategoryFrame() {
        setTitle("Quản lý danh mục"); setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560)); setSize(950, 660); setLocationRelativeTo(null);
        buildUi(); loadCategories("");
    }

    private void buildUi() {
        JPanel root = Ui.pagePanel(); setContentPane(root);
        root.add(Ui.header("Quản lý danh mục sản phẩm", "Tạo nhóm hàng như điện thoại, phụ kiện hoặc thiết bị đeo."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14)); body.setOpaque(false);

        JPanel form = Ui.whiteCard();
        JLabel title = new JLabel("THÔNG TIN DANH MỤC"); title.setForeground(Ui.NAVY); title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        form.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(1, 2, 12, 8)); fields.setOpaque(false);
        fields.add(fieldBox("Tên danh mục *", txtName)); fields.add(fieldBox("Mô tả", txtDescription));
        form.add(fields, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2)); actions.setOpaque(false);
        JButton add = Ui.button("＋ Thêm mới", Ui.GREEN), update = Ui.button("✎ Cập nhật", Ui.BLUE),
                delete = Ui.button("⌫ Xóa", Ui.RED), clear = Ui.button("Làm mới form", new Color(104, 117, 136));
        add.addActionListener(e -> addCategory()); update.addActionListener(e -> updateCategory()); delete.addActionListener(e -> deleteCategory()); clear.addActionListener(e -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear); form.add(actions, BorderLayout.SOUTH);
        body.add(form, BorderLayout.NORTH);

        JPanel list = Ui.whiteCard();
        JPanel searchBar = new JPanel(new BorderLayout(10, 0)); searchBar.setOpaque(false);
        JLabel listTitle = new JLabel("DANH SÁCH DANH MỤC"); listTitle.setForeground(Ui.NAVY); listTitle.setFont(listTitle.getFont().deriveFont(Font.BOLD, 13f));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); controls.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(245, 34)); txtSearch.setToolTipText("Tìm theo tên hoặc mô tả");
        JButton search = Ui.button("Tìm kiếm", Ui.BLUE), reload = Ui.button("Tải lại", new Color(104, 117, 136));
        search.addActionListener(e -> loadCategories(txtSearch.getText().trim())); txtSearch.addActionListener(e -> loadCategories(txtSearch.getText().trim()));
        reload.addActionListener(e -> { txtSearch.setText(""); loadCategories(""); });
        controls.add(txtSearch); controls.add(search); controls.add(reload); searchBar.add(listTitle, BorderLayout.WEST); searchBar.add(controls, BorderLayout.EAST);
        list.add(searchBar, BorderLayout.NORTH);
        Ui.styleTable(table); table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFromSelectedRow(); });
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel tip = new JLabel("Danh mục đang được sản phẩm sử dụng sẽ không thể xóa."); tip.setForeground(Ui.MUTED); tip.setFont(tip.getFont().deriveFont(11.5f)); list.add(tip, BorderLayout.SOUTH);
        body.add(list, BorderLayout.CENTER); root.add(body, BorderLayout.CENTER);
    }

    private JPanel fieldBox(String label, Component field) {
        JPanel box = new JPanel(new BorderLayout(0, 5)); box.setOpaque(false); box.add(Ui.fieldLabel(label), BorderLayout.NORTH); box.add(field, BorderLayout.CENTER); return box;
    }

    private void loadCategories(String keyword) {
        String sql = "SELECT MaDanhMuc, TenDanhMuc, ISNULL(MoTa,'') FROM dbo.DanhMuc WHERE TenDanhMuc LIKE ? OR ISNULL(MoTa,'') LIKE ? ORDER BY MaDanhMuc DESC";
        String like = "%" + keyword + "%"; model.setRowCount(0);
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)}); }
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private String[] validateForm() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) { Ui.showError(this, "Tên danh mục là bắt buộc."); return null; }
        return new String[]{name, blankToNull(txtDescription.getText())};
    }

    private void addCategory() {
        String[] data = validateForm(); if (data == null) return;
        String sql = "INSERT INTO dbo.DanhMuc (TenDanhMuc, MoTa) VALUES (?, ?)";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setNString(1, data[0]); setNullableNString(ps, 2, data[1]); ps.executeUpdate();
            Ui.showSuccess(this, "Đã thêm danh mục."); clearForm(); loadCategories(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void updateCategory() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn danh mục trong bảng trước khi cập nhật."); return; }
        String[] data = validateForm(); if (data == null) return;
        String sql = "UPDATE dbo.DanhMuc SET TenDanhMuc=?, MoTa=? WHERE MaDanhMuc=?";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setNString(1, data[0]); setNullableNString(ps, 2, data[1]); ps.setInt(3, selectedId); ps.executeUpdate();
            Ui.showSuccess(this, "Đã cập nhật danh mục."); clearForm(); loadCategories(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void deleteCategory() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn danh mục trong bảng trước khi xóa."); return; }
        if (!Ui.confirmDelete(this, "danh mục mã " + selectedId)) return;
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM dbo.DanhMuc WHERE MaDanhMuc=?")) {
            ps.setInt(1, selectedId); ps.executeUpdate(); Ui.showSuccess(this, "Đã xóa danh mục."); clearForm(); loadCategories(txtSearch.getText().trim());
        } catch (SQLException ex) { Ui.showSqlError(this, ex); }
    }

    private void setNullableNString(PreparedStatement ps, int index, String text) throws SQLException {
        if (text == null) ps.setNull(index, java.sql.Types.NVARCHAR); else ps.setNString(index, text);
    }

    private void fillFromSelectedRow() {
        int view = table.getSelectedRow(); if (view < 0) return; int row = table.convertRowIndexToModel(view);
        selectedId = ((Number) model.getValueAt(row, 0)).intValue(); txtName.setText(String.valueOf(model.getValueAt(row, 1))); txtDescription.setText(String.valueOf(model.getValueAt(row, 2)));
    }

    private void clearForm() { selectedId = null; table.clearSelection(); txtName.setText(""); txtDescription.setText(""); txtName.requestFocusInWindow(); }
    private String blankToNull(String text) { String value = text == null ? "" : text.trim(); return value.isEmpty() ? null : value; }
}
