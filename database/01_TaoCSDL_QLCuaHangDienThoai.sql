/*
  DE TAI: QUAN LY CUA HANG DIEN THOAI
  He quan tri CSDL: Microsoft SQL Server
  File nay tao CSDL va 10 bang cung cac rang buoc chinh.
  Luu y: chay tren SQL Server/SSMS. Nen chay tren CSDL moi.
*/

IF DB_ID(N'QLCuaHangDienThoai') IS NULL
BEGIN
    CREATE DATABASE QLCuaHangDienThoai;
END;
GO

USE QLCuaHangDienThoai;
GO

/* 1. Danh muc san pham */
CREATE TABLE dbo.DanhMuc
(
    MaDanhMuc INT IDENTITY(1,1) NOT NULL,
    TenDanhMuc NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255) NULL,
    CONSTRAINT PK_DanhMuc PRIMARY KEY (MaDanhMuc),
    CONSTRAINT UQ_DanhMuc_Ten UNIQUE (TenDanhMuc)
);
GO

/* 2. Nha cung cap */
CREATE TABLE dbo.NhaCungCap
(
    MaNCC INT IDENTITY(1,1) NOT NULL,
    TenNCC NVARCHAR(150) NOT NULL,
    SoDienThoai VARCHAR(15) NULL,
    Email VARCHAR(100) NULL,
    DiaChi NVARCHAR(255) NULL,
    CONSTRAINT PK_NhaCungCap PRIMARY KEY (MaNCC)
);
GO
CREATE UNIQUE INDEX UX_NhaCungCap_SDT
    ON dbo.NhaCungCap(SoDienThoai) WHERE SoDienThoai IS NOT NULL;
GO

/* 3. Khach hang */
CREATE TABLE dbo.KhachHang
(
    MaKH INT IDENTITY(1,1) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NULL,
    DiaChi NVARCHAR(255) NULL,
    NgayTao DATETIME2(0) NOT NULL CONSTRAINT DF_KhachHang_NgayTao DEFAULT (SYSDATETIME()),
    CONSTRAINT PK_KhachHang PRIMARY KEY (MaKH),
    CONSTRAINT UQ_KhachHang_SDT UNIQUE (SoDienThoai)
);
GO

/* 4. Nhan vien */
CREATE TABLE dbo.NhanVien
(
    MaNV INT IDENTITY(1,1) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NULL,
    GioiTinh NVARCHAR(10) NULL,
    SoDienThoai VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NULL,
    DiaChi NVARCHAR(255) NULL,
    Luong DECIMAL(18,2) NOT NULL CONSTRAINT DF_NhanVien_Luong DEFAULT (0),
    TrangThai BIT NOT NULL CONSTRAINT DF_NhanVien_TrangThai DEFAULT (1),
    CONSTRAINT PK_NhanVien PRIMARY KEY (MaNV),
    CONSTRAINT UQ_NhanVien_SDT UNIQUE (SoDienThoai),
    CONSTRAINT CK_NhanVien_Luong CHECK (Luong >= 0),
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (GioiTinh IS NULL OR GioiTinh IN (N'Nam', N'Nữ', N'Khác'))
);
GO

/* 5. Tai khoan dang nhap. Moi nhan vien toi da co mot tai khoan. */
CREATE TABLE dbo.TaiKhoan
(
    MaTK INT IDENTITY(1,1) NOT NULL,
    MaNV INT NOT NULL,
    TenDangNhap VARCHAR(50) NOT NULL,
    MatKhauHash VARCHAR(255) NOT NULL,
    VaiTro VARCHAR(20) NOT NULL,
    TrangThai BIT NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT (1),
    CONSTRAINT PK_TaiKhoan PRIMARY KEY (MaTK),
    CONSTRAINT UQ_TaiKhoan_TenDangNhap UNIQUE (TenDangNhap),
    CONSTRAINT UQ_TaiKhoan_MaNV UNIQUE (MaNV),
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (MaNV) REFERENCES dbo.NhanVien(MaNV),
    CONSTRAINT CK_TaiKhoan_VaiTro CHECK (VaiTro IN ('ADMIN', 'NHANVIEN'))
);
GO

/* 6. San pham */
CREATE TABLE dbo.SanPham
(
    MaSP INT IDENTITY(1,1) NOT NULL,
    MaDanhMuc INT NOT NULL,
    TenSP NVARCHAR(150) NOT NULL,
    HangSanXuat NVARCHAR(100) NOT NULL,
    Model NVARCHAR(100) NULL,
    DonGiaBan DECIMAL(18,2) NOT NULL,
    SoLuongTon INT NOT NULL CONSTRAINT DF_SanPham_SoLuongTon DEFAULT (0),
    BaoHanhThang INT NOT NULL CONSTRAINT DF_SanPham_BaoHanh DEFAULT (12),
    TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_SanPham_TrangThai DEFAULT (N'DANG_BAN'),
    CONSTRAINT PK_SanPham PRIMARY KEY (MaSP),
    CONSTRAINT FK_SanPham_DanhMuc FOREIGN KEY (MaDanhMuc) REFERENCES dbo.DanhMuc(MaDanhMuc),
    CONSTRAINT CK_SanPham_DonGia CHECK (DonGiaBan >= 0),
    CONSTRAINT CK_SanPham_SoLuongTon CHECK (SoLuongTon >= 0),
    CONSTRAINT CK_SanPham_BaoHanh CHECK (BaoHanhThang >= 0),
    CONSTRAINT CK_SanPham_TrangThai CHECK (TrangThai IN (N'DANG_BAN', N'NGUNG_BAN'))
);
GO
CREATE INDEX IX_SanPham_TenSP ON dbo.SanPham(TenSP);
CREATE INDEX IX_SanPham_MaDanhMuc ON dbo.SanPham(MaDanhMuc);
GO

/* 7. Phieu nhap hang */
CREATE TABLE dbo.PhieuNhap
(
    MaPN INT IDENTITY(1,1) NOT NULL,
    MaNCC INT NOT NULL,
    MaNV INT NOT NULL,
    NgayNhap DATETIME2(0) NOT NULL CONSTRAINT DF_PhieuNhap_NgayNhap DEFAULT (SYSDATETIME()),
    TongTien DECIMAL(18,2) NOT NULL CONSTRAINT DF_PhieuNhap_TongTien DEFAULT (0),
    GhiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_PhieuNhap PRIMARY KEY (MaPN),
    CONSTRAINT FK_PhieuNhap_NhaCungCap FOREIGN KEY (MaNCC) REFERENCES dbo.NhaCungCap(MaNCC),
    CONSTRAINT FK_PhieuNhap_NhanVien FOREIGN KEY (MaNV) REFERENCES dbo.NhanVien(MaNV),
    CONSTRAINT CK_PhieuNhap_TongTien CHECK (TongTien >= 0)
);
GO

/* 8. Chi tiet phieu nhap */
CREATE TABLE dbo.ChiTietPhieuNhap
(
    MaPN INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT NOT NULL,
    DonGiaNhap DECIMAL(18,2) NOT NULL,
    ThanhTien AS CONVERT(DECIMAL(18,2), SoLuong * DonGiaNhap) PERSISTED,
    CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (MaPN, MaSP),
    CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (MaPN) REFERENCES dbo.PhieuNhap(MaPN),
    CONSTRAINT FK_CTPN_SanPham FOREIGN KEY (MaSP) REFERENCES dbo.SanPham(MaSP),
    CONSTRAINT CK_CTPN_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_CTPN_DonGiaNhap CHECK (DonGiaNhap >= 0)
);
GO

/* 9. Hoa don ban hang */
CREATE TABLE dbo.HoaDon
(
    MaHD INT IDENTITY(1,1) NOT NULL,
    MaKH INT NULL,
    MaNV INT NOT NULL,
    NgayLap DATETIME2(0) NOT NULL CONSTRAINT DF_HoaDon_NgayLap DEFAULT (SYSDATETIME()),
    TongTien DECIMAL(18,2) NOT NULL CONSTRAINT DF_HoaDon_TongTien DEFAULT (0),
    TrangThai NVARCHAR(20) NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT (N'DA_THANH_TOAN'),
    GhiChu NVARCHAR(255) NULL,
    CONSTRAINT PK_HoaDon PRIMARY KEY (MaHD),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKH) REFERENCES dbo.KhachHang(MaKH),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (MaNV) REFERENCES dbo.NhanVien(MaNV),
    CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
    CONSTRAINT CK_HoaDon_TrangThai CHECK (TrangThai IN (N'DA_THANH_TOAN', N'CHO_THANH_TOAN', N'DA_HUY'))
);
GO
CREATE INDEX IX_HoaDon_NgayLap ON dbo.HoaDon(NgayLap);
CREATE INDEX IX_HoaDon_MaKH ON dbo.HoaDon(MaKH);
GO

/* 10. Chi tiet hoa don. GiamGia la tong tien giam tren mot dong san pham. */
CREATE TABLE dbo.ChiTietHoaDon
(
    MaHD INT NOT NULL,
    MaSP INT NOT NULL,
    SoLuong INT NOT NULL,
    DonGiaBan DECIMAL(18,2) NOT NULL,
    GiamGia DECIMAL(18,2) NOT NULL CONSTRAINT DF_CTHD_GiamGia DEFAULT (0),
    ThanhTien AS CONVERT(DECIMAL(18,2), (SoLuong * DonGiaBan) - GiamGia) PERSISTED,
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (MaHD, MaSP),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (MaHD) REFERENCES dbo.HoaDon(MaHD),
    CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (MaSP) REFERENCES dbo.SanPham(MaSP),
    CONSTRAINT CK_CTHD_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_CTHD_DonGiaBan CHECK (DonGiaBan >= 0),
    CONSTRAINT CK_CTHD_GiamGia CHECK (GiamGia >= 0 AND GiamGia <= SoLuong * DonGiaBan)
);
GO

PRINT N'Da tao xong CSDL QLCuaHangDienThoai va 10 bang.';
PRINT N'Tiep theo, mo va chay file 02_DuLieuMau.sql de them du lieu kiem thu.';
GO
