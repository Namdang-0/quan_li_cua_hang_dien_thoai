/*
  DU LIEU MAU CHO QLCuaHangDienThoai
  Dieu kien: da chay 01_TaoCSDL_QLCuaHangDienThoai.sql tren CSDL moi.
  Chi chay file nay mot lan de tranh tao du lieu giao dich trung lap.
*/
USE QLCuaHangDienThoai;
GO

IF EXISTS (SELECT 1 FROM dbo.DanhMuc)
BEGIN
    THROW 50001, N'CSDL da co du lieu. Hay dung CSDL moi hoac xoa du lieu mau truoc khi chay lai file nay.', 1;
END;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    /* Danh muc: ID du kien tu 1 den 3 */
    INSERT INTO dbo.DanhMuc (TenDanhMuc, MoTa) VALUES
    (N'Dien thoai', N'Dien thoai thong minh cac hang'),
    (N'Phu kien', N'Tai nghe, cap sac va phu kien di kem'),
    (N'Thiet bi deo', N'Dong ho thong minh va thiet bi deo');

    /* Nha cung cap: ID 1-2 */
    INSERT INTO dbo.NhaCungCap (TenNCC, SoDienThoai, Email, DiaChi) VALUES
    (N'Nha cung cap Minh Phat', '0908000101', 'minhphat@example.com', N'Ha Noi'),
    (N'Nha cung cap An Tin', '0908000102', 'antin@example.com', N'Bac Ninh');

    /* Khach hang: ID 1-4 */
    INSERT INTO dbo.KhachHang (HoTen, SoDienThoai, Email, DiaChi) VALUES
    (N'Nguyen Van An', '0912000001', 'an@example.com', N'Ha Noi'),
    (N'Tran Thi Binh', '0912000002', 'binh@example.com', N'Ha Noi'),
    (N'Le Quang Huy', '0912000003', 'huy@example.com', N'Ha Dong, Ha Noi'),
    (N'Pham Thu Trang', '0912000004', NULL, N'Bac Ninh');

    /* Nhan vien: ID 1-3 */
    INSERT INTO dbo.NhanVien (HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, DiaChi, Luong, TrangThai) VALUES
    (N'Nguyen Minh Quan', '2002-05-12', N'Nam', '0987000101', 'quan@example.com', N'Ha Noi', 12000000, 1),
    (N'Tran Ngoc Mai', '2003-09-21', N'Nữ', '0987000102', 'mai@example.com', N'Ha Noi', 9000000, 1),
    (N'Le Duc Long', '2001-11-04', N'Nam', '0987000103', 'long@example.com', N'Ha Dong, Ha Noi', 9500000, 1);

    /* Tai khoan mau. MatKhauHash duoc ma hoa PBKDF2-HMAC-SHA256.
       Hash PBKDF2-HMAC-SHA256, 150000 vong; xem mat khau demo trong README. */
    INSERT INTO dbo.TaiKhoan (MaNV, TenDangNhap, MatKhauHash, VaiTro, TrangThai) VALUES
    (1, 'admin', 'PBKDF2WithHmacSHA256$150000$YWRtaW4tZGVtby1zYWx0LTAx$FCVIwovuYUUnAML2y70nb5hjr9XNW0XNc9G9mdf+WBk=', 'ADMIN', 1),
    (2, 'nvmai', 'PBKDF2WithHmacSHA256$150000$bnZtYWktZGVtby1zYWx0LTAy$ubRjpkhtoRX5XtTiCCXbqFEOniD0C9tXKOY3qnnrwmk=', 'NHANVIEN', 1),
    (3, 'nvlong', 'PBKDF2WithHmacSHA256$150000$bnZsb25nLWRlbW8tc2FsdC0wMw==$KrrJ/Y8CYXUefYdtBYJvEJsXNNzgaro4oSirjcb+VXg=', 'NHANVIEN', 1);

    /* San pham: ID 1-8. Ton kho se duoc cap nhat tu phieu nhap va hoa don mau phia duoi. */
    INSERT INTO dbo.SanPham
        (MaDanhMuc, TenSP, HangSanXuat, Model, DonGiaBan, SoLuongTon, BaoHanhThang, TrangThai)
    VALUES
    (1, N'iPhone 16 128GB', N'Apple', N'iPhone 16 128GB', 21990000, 0, 12, N'DANG_BAN'),
    (1, N'Samsung Galaxy S25 256GB', N'Samsung', N'Galaxy S25 256GB', 22500000, 0, 12, N'DANG_BAN'),
    (1, N'Xiaomi Redmi Note 14 Pro', N'Xiaomi', N'Redmi Note 14 Pro', 7490000, 0, 12, N'DANG_BAN'),
    (1, N'iPhone 15 128GB', N'Apple', N'iPhone 15 128GB', 17490000, 0, 12, N'DANG_BAN'),
    (1, N'Samsung Galaxy A56', N'Samsung', N'Galaxy A56', 8290000, 0, 12, N'DANG_BAN'),
    (1, N'Xiaomi POCO X7', N'Xiaomi', N'POCO X7', 7990000, 0, 12, N'DANG_BAN'),
    (2, N'Apple AirPods 4', N'Apple', N'AirPods 4', 3490000, 0, 12, N'DANG_BAN'),
    (2, N'Samsung Galaxy Buds FE', N'Samsung', N'Galaxy Buds FE', 1490000, 0, 12, N'DANG_BAN');

    /* Phieu nhap: ID 1-2 */
    INSERT INTO dbo.PhieuNhap (MaNCC, MaNV, NgayNhap, TongTien, GhiChu) VALUES
    (1, 1, '2026-09-01T09:00:00', 0, N'Nhap hang dot 1'),
    (2, 1, '2026-09-10T10:30:00', 0, N'Nhap hang dot 2');

    INSERT INTO dbo.ChiTietPhieuNhap (MaPN, MaSP, SoLuong, DonGiaNhap) VALUES
    (1, 1, 5, 18500000),
    (1, 2, 4, 19000000),
    (1, 3, 10, 5700000),
    (1, 4, 6, 14800000),
    (2, 5, 8, 6500000),
    (2, 6, 5, 6200000),
    (2, 7, 6, 2600000),
    (2, 8, 7, 1050000);

    /* Cap nhat tong phieu nhap va tang ton kho theo so luong da nhap. */
    UPDATE pn
       SET TongTien = x.TongTien
    FROM dbo.PhieuNhap pn
    JOIN (
        SELECT MaPN, SUM(ThanhTien) AS TongTien
        FROM dbo.ChiTietPhieuNhap
        GROUP BY MaPN
    ) x ON x.MaPN = pn.MaPN;

    UPDATE sp
       SET SoLuongTon = sp.SoLuongTon + x.SoLuongNhap
    FROM dbo.SanPham sp
    JOIN (
        SELECT MaSP, SUM(SoLuong) AS SoLuongNhap
        FROM dbo.ChiTietPhieuNhap
        GROUP BY MaSP
    ) x ON x.MaSP = sp.MaSP;

    /* Hoa don: ID 1-2 */
    INSERT INTO dbo.HoaDon (MaKH, MaNV, NgayLap, TongTien, TrangThai, GhiChu) VALUES
    (1, 2, '2026-09-15T14:20:00', 0, N'DA_THANH_TOAN', N'Hoa don ban le'),
    (2, 2, '2026-09-16T16:10:00', 0, N'DA_THANH_TOAN', N'Hoa don ban le');

    INSERT INTO dbo.ChiTietHoaDon (MaHD, MaSP, SoLuong, DonGiaBan, GiamGia) VALUES
    (1, 1, 1, 21990000, 0),
    (1, 7, 1, 3490000, 0),
    (2, 3, 2, 7490000, 200000),
    (2, 5, 1, 8290000, 0);

    /* Tong tien hoa don = tong thanh tien cac dong chi tiet. */
    UPDATE hd
       SET TongTien = x.TongTien
    FROM dbo.HoaDon hd
    JOIN (
        SELECT MaHD, SUM(ThanhTien) AS TongTien
        FROM dbo.ChiTietHoaDon
        GROUP BY MaHD
    ) x ON x.MaHD = hd.MaHD;

    /* Tru ton kho theo hang da ban. */
    UPDATE sp
       SET SoLuongTon = sp.SoLuongTon - x.SoLuongBan
    FROM dbo.SanPham sp
    JOIN (
        SELECT MaSP, SUM(SoLuong) AS SoLuongBan
        FROM dbo.ChiTietHoaDon
        GROUP BY MaSP
    ) x ON x.MaSP = sp.MaSP;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

/* Truy van kiem tra sau khi nap du lieu mau */
SELECT MaSP, TenSP, HangSanXuat, DonGiaBan, SoLuongTon FROM dbo.SanPham ORDER BY MaSP;
SELECT MaHD, NgayLap, MaKH, MaNV, TongTien, TrangThai FROM dbo.HoaDon ORDER BY MaHD;
SELECT MaPN, NgayNhap, MaNCC, MaNV, TongTien FROM dbo.PhieuNhap ORDER BY MaPN;
GO
