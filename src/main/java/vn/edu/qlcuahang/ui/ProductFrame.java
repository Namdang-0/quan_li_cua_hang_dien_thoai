package vn.edu.qlcuahang.ui;

import vn.edu.qlcuahang.config.Db;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Form CRUD và tìm kiếm sản phẩm. */
public class ProductFrame extends JFrame {
    private final JTextField txtSearch = Ui.textField();
    private final JTextField txtName = Ui.textField();
    private final JTextField txtBrand = Ui.textField();
    private final JTextField txtModel = Ui.textField();
    private final JTextField txtPrice = Ui.textField();
    private final JTextField txtStock = Ui.textField();
    private final JTextField txtWarranty = Ui.textField();
    private final JComboBox<CategoryItem> cboCategory = new JComboBox<>();
    private final JComboBox<String> cboStatus = new JComboBox<>(new String[]{"DANG_BAN", "NGUNG_BAN"});
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã SP", "Mã danh mục (ẩn)", "Tên sản phẩm", "Hãng", "Model", "Danh mục", "Đơn giá", "Tồn kho", "BH (tháng)", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private Integer selectedId;

    public ProductFrame() {
        setTitle("Quản lý sản phẩm");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1220, 800);
        setLocationRelativeTo(null);
        buildUi();
        loadCategories();
        loadProducts("");
    }

    private void buildUi() {
        JPanel root = Ui.pagePanel();
        setContentPane(root);
        root.add(Ui.header("Quản lý sản phẩm", "Thêm, chỉnh sửa, xóa và tìm kiếm điện thoại trong kho hàng."), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        JPanel formCard = Ui.whiteCard();
        JLabel formTitle = new JLabel("THÔNG TIN SẢN PHẨM");
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 13f));
        formTitle.setForeground(Ui.NAVY);
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(4, 2, 12, 9));
        fields.setOpaque(false);
        fields.add(fieldBox("Danh mục *", cboCategory));
        fields.add(fieldBox("Tên sản phẩm *", txtName));
        fields.add(fieldBox("Hãng sản xuất *", txtBrand));
        fields.add(fieldBox("Model / phiên bản", txtModel));
        fields.add(fieldBox("Đơn giá (VNĐ) *", txtPrice));
        fields.add(fieldBox("Số lượng tồn *", txtStock));
        fields.add(fieldBox("Bảo hành (tháng) *", txtWarranty));
        fields.add(fieldBox("Trạng thái", cboStatus));
        formCard.add(fields, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        actions.setOpaque(false);
        JButton add = Ui.button("＋ Thêm mới", Ui.GREEN);
        JButton update = Ui.button("✎ Cập nhật", Ui.BLUE);
        JButton delete = Ui.button("⌫ Xóa", Ui.RED);
        JButton clear = Ui.button("Làm mới form", new Color(104, 117, 136));
        add.addActionListener(e -> addProduct());
        update.addActionListener(e -> updateProduct());
        delete.addActionListener(e -> deleteProduct());
        clear.addActionListener(e -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        formCard.add(actions, BorderLayout.SOUTH);
        body.add(formCard, BorderLayout.NORTH);

        JPanel tableCard = Ui.whiteCard();
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        JLabel listTitle = new JLabel("DANH SÁCH SẢN PHẨM");
        listTitle.setForeground(Ui.NAVY);
        listTitle.setFont(listTitle.getFont().deriveFont(Font.BOLD, 13f));
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchControls.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(260, 34));
        txtSearch.setToolTipText("Tìm theo tên, hãng, model hoặc danh mục");
        JButton search = Ui.button("Tìm kiếm", Ui.BLUE);
        JButton reload = Ui.button("Tải lại", new Color(104, 117, 136));
        search.addActionListener(e -> loadProducts(txtSearch.getText().trim()));
        txtSearch.addActionListener(e -> loadProducts(txtSearch.getText().trim()));
        reload.addActionListener(e -> { txtSearch.setText(""); loadProducts(""); });
        searchControls.add(txtSearch); searchControls.add(search); searchControls.add(reload);
        searchBar.add(listTitle, BorderLayout.WEST);
        searchBar.add(searchControls, BorderLayout.EAST);
        tableCard.add(searchBar, BorderLayout.NORTH);

        Ui.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setPreferredWidth(0);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillFromSelectedRow();
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Ui.BORDER));
        tableCard.add(scroll, BorderLayout.CENTER);
        JLabel tip = new JLabel("Mẹo: chọn một dòng trong bảng để nạp dữ liệu lên biểu mẫu.");
        tip.setForeground(Ui.MUTED);
        tip.setFont(tip.getFont().deriveFont(11.5f));
        tableCard.add(tip, BorderLayout.SOUTH);
        body.add(tableCard, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);
    }

    private JPanel fieldBox(String label, Component field) {
        JPanel box = new JPanel(new BorderLayout(0, 5));
        box.setOpaque(false);
        box.add(Ui.fieldLabel(label), BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    private void loadCategories() {
        cboCategory.removeAllItems();
        try (Connection con = Db.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT MaDanhMuc, TenDanhMuc FROM dbo.DanhMuc ORDER BY TenDanhMuc");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cboCategory.addItem(new CategoryItem(rs.getInt("MaDanhMuc"), rs.getString("TenDanhMuc")));
        } catch (SQLException ex) {
            Ui.showSqlError(this, ex);
        }
    }

    private void loadProducts(String keyword) {
        String sql = "SELECT p.MaSP, p.MaDanhMuc, p.TenSP, p.HangSanXuat, ISNULL(p.Model, N'') AS Model, "
                + "d.TenDanhMuc, p.DonGiaBan, p.SoLuongTon, p.BaoHanhThang, p.TrangThai "
                + "FROM dbo.SanPham p INNER JOIN dbo.DanhMuc d ON d.MaDanhMuc = p.MaDanhMuc "
                + "WHERE p.TenSP LIKE ? OR p.HangSanXuat LIKE ? OR ISNULL(p.Model, N'') LIKE ? OR d.TenDanhMuc LIKE ? "
                + "ORDER BY p.MaSP DESC";
        String like = "%" + keyword + "%";
        tableModel.setRowCount(0);
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getInt("MaSP"), rs.getInt("MaDanhMuc"), rs.getString("TenSP"),
                            rs.getString("HangSanXuat"), rs.getString("Model"), rs.getString("TenDanhMuc"),
                            rs.getBigDecimal("DonGiaBan"), rs.getInt("SoLuongTon"), rs.getInt("BaoHanhThang"), rs.getString("TrangThai")});
                }
            }
        } catch (SQLException ex) {
            Ui.showSqlError(this, ex);
        }
    }

    private void addProduct() {
        ProductInput input = validateForm();
        if (input == null) return;
        String sql = "INSERT INTO dbo.SanPham (MaDanhMuc, TenSP, HangSanXuat, Model, DonGiaBan, SoLuongTon, BaoHanhThang, TrangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bindProduct(ps, input);
            ps.executeUpdate();
            Ui.showSuccess(this, "Đã thêm sản phẩm thành công.");
            clearForm(); loadProducts(txtSearch.getText().trim());
        } catch (SQLException ex) {
            Ui.showSqlError(this, ex);
        }
    }

    private void updateProduct() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn sản phẩm trong bảng trước khi cập nhật."); return; }
        ProductInput input = validateForm();
        if (input == null) return;
        String sql = "UPDATE dbo.SanPham SET MaDanhMuc=?, TenSP=?, HangSanXuat=?, Model=?, DonGiaBan=?, "
                + "SoLuongTon=?, BaoHanhThang=?, TrangThai=? WHERE MaSP=?";
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bindProduct(ps, input);
            ps.setInt(9, selectedId);
            int affected = ps.executeUpdate();
            if (affected == 0) Ui.showError(this, "Không tìm thấy sản phẩm cần cập nhật.");
            else Ui.showSuccess(this, "Đã cập nhật sản phẩm thành công.");
            clearForm(); loadProducts(txtSearch.getText().trim());
        } catch (SQLException ex) {
            Ui.showSqlError(this, ex);
        }
    }

    private void deleteProduct() {
        if (selectedId == null) { Ui.showError(this, "Hãy chọn sản phẩm trong bảng trước khi xóa."); return; }
        if (!Ui.confirmDelete(this, "sản phẩm mã " + selectedId)) return;
        try (Connection con = Db.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM dbo.SanPham WHERE MaSP=?")) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            Ui.showSuccess(this, "Đã xóa sản phẩm.");
            clearForm(); loadProducts(txtSearch.getText().trim());
        } catch (SQLException ex) {
            Ui.showSqlError(this, ex);
        }
    }

    private ProductInput validateForm() {
        CategoryItem category = (CategoryItem) cboCategory.getSelectedItem();
        if (category == null) { Ui.showError(this, "Chưa có danh mục. Hãy tạo danh mục trước ở màn hình Quản lý danh mục."); return null; }
        String name = txtName.getText().trim();
        String brand = txtBrand.getText().trim();
        if (name.isEmpty() || brand.isEmpty()) { Ui.showError(this, "Tên sản phẩm và hãng sản xuất không được để trống."); return null; }
        try {
            String rawPrice = txtPrice.getText().trim().replaceAll("[.,\\s]", "");
            if (rawPrice.isEmpty()) throw new NumberFormatException();
            BigDecimal price = new BigDecimal(rawPrice);
            int stock = Integer.parseInt(txtStock.getText().trim());
            int warranty = Integer.parseInt(txtWarranty.getText().trim());
            if (price.signum() < 0 || stock < 0 || warranty < 0) {
                Ui.showError(this, "Đơn giá, số lượng tồn và thời hạn bảo hành phải lớn hơn hoặc bằng 0."); return null;
            }
            return new ProductInput(category.id, name, brand, blankToNull(txtModel.getText()), price, stock, warranty,
                    (String) cboStatus.getSelectedItem());
        } catch (NumberFormatException ex) {
            Ui.showError(this, "Đơn giá phải là số; số lượng tồn và bảo hành phải là số nguyên hợp lệ. Có thể nhập giá như 21990000 hoặc 21.990.000.");
            return null;
        }
    }

    private void bindProduct(PreparedStatement ps, ProductInput in) throws SQLException {
        ps.setInt(1, in.categoryId);
        ps.setNString(2, in.name);
        ps.setNString(3, in.brand);
        if (in.model == null) ps.setNull(4, java.sql.Types.NVARCHAR); else ps.setNString(4, in.model);
        ps.setBigDecimal(5, in.price);
        ps.setInt(6, in.stock);
        ps.setInt(7, in.warranty);
        ps.setNString(8, in.status);
    }

    private void fillFromSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        selectedId = ((Number) tableModel.getValueAt(row, 0)).intValue();
        int categoryId = ((Number) tableModel.getValueAt(row, 1)).intValue();
        txtName.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtBrand.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtModel.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        Object price = tableModel.getValueAt(row, 6);
        txtPrice.setText(price instanceof BigDecimal b ? b.stripTrailingZeros().toPlainString() : String.valueOf(price));
        txtStock.setText(String.valueOf(tableModel.getValueAt(row, 7)));
        txtWarranty.setText(String.valueOf(tableModel.getValueAt(row, 8)));
        cboStatus.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 9)));
        for (int i = 0; i < cboCategory.getItemCount(); i++) {
            if (cboCategory.getItemAt(i).id == categoryId) { cboCategory.setSelectedIndex(i); break; }
        }
    }

    private void clearForm() {
        selectedId = null;
        table.clearSelection();
        txtName.setText(""); txtBrand.setText(""); txtModel.setText("");
        txtPrice.setText(""); txtStock.setText("0"); txtWarranty.setText("12");
        cboStatus.setSelectedItem("DANG_BAN");
        if (cboCategory.getItemCount() > 0) cboCategory.setSelectedIndex(0);
        txtName.requestFocusInWindow();
    }

    private String blankToNull(String text) {
        String value = text == null ? "" : text.trim();
        return value.isEmpty() ? null : value;
    }

    private static class CategoryItem {
        private final int id;
        private final String name;
        private CategoryItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    private record ProductInput(int categoryId, String name, String brand, String model, BigDecimal price,
                                int stock, int warranty, String status) { }
}
