# Phần mềm quản lý cửa hàng điện thoại

Đồ án Java Swing kết nối Microsoft SQL Server qua JDBC. Dự án có 4 form dữ liệu, mỗi form hỗ trợ **thêm, sửa, xóa, tìm kiếm và tải lại**:

1. Quản lý sản phẩm.
2. Quản lý khách hàng.
3. Quản lý nhà cung cấp.
4. Quản lý danh mục sản phẩm.

Các form sử dụng `PreparedStatement`, kiểm tra dữ liệu đầu vào và hiển thị thông báo khi vi phạm ràng buộc khóa ngoại/duy nhất. Giao diện dùng FlatLaf để có giao diện Swing hiện đại hơn.

## Công nghệ

- Java 17 trở lên
- Java Swing
- Microsoft SQL Server
- JDBC Driver for SQL Server `13.6.2.jre11`
- Maven
- FlatLaf `3.8`

## 1. Tạo cơ sở dữ liệu

Mở SQL Server Management Studio (SSMS), kết nối đúng instance SQL Server rồi chạy **theo thứ tự**:

1. `database/01_TaoCSDL_QLCuaHangDienThoai.sql` — tạo cơ sở dữ liệu và 10 bảng.
2. `database/02_DuLieuMau.sql` — thêm dữ liệu minh họa để thử ứng dụng.

File dữ liệu mẫu chỉ nên chạy một lần trên database mới. Nếu bạn đã có dữ liệu thật trong database, không chạy lại file mẫu.

## 2. Cấu hình kết nối SQL Server

1. Sao chép `config/db.properties.example` thành `config/db.properties`.
2. Mở `config/db.properties`, sửa URL, tên đăng nhập và mật khẩu theo máy của bạn.
3. Không đẩy `config/db.properties` lên GitHub — file này đã được thêm vào `.gitignore`.

Cấu hình mẫu dùng SQL Server đang lắng nghe TCP ở cổng `1433`:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=QLCuaHangDienThoai;encrypt=true;trustServerCertificate=true;loginTimeout=5;
db.user=sa
db.password=CHANGE_ME
```

Thay `CHANGE_ME` bằng mật khẩu SQL Server trên máy của bạn. Không dùng nguyên mật khẩu mẫu.

Nếu dùng SQL Server Express có instance `SQLEXPRESS`, thay URL bằng:

```properties
db.url=jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=QLCuaHangDienThoai;encrypt=true;trustServerCertificate=true;loginTimeout=5;
```

Đồng thời đặt `db.user` và `db.password` đúng với tài khoản SQL Server. Nếu instance không cho kết nối qua TCP hoặc đăng nhập SQL, hãy kiểm tra SQL Server Configuration Manager, TCP/IP, cổng, chế độ xác thực và quyền của tài khoản. Ứng dụng hiện dùng SQL Server Authentication; tài khoản `sa` chỉ là ví dụ, không nên dùng mật khẩu yếu hoặc dùng chung trên môi trường thực tế.

Có thể truyền cấu hình qua biến môi trường thay vì file:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

Biến môi trường có ưu tiên cao hơn `config/db.properties`.

## 3. Chạy trong IntelliJ IDEA

1. Cài JDK 17 trở lên và Maven (hoặc dùng Maven được tích hợp trong IntelliJ).
2. Mở thư mục chứa `pom.xml` dưới dạng dự án Maven.
3. Chờ IDE tải dependencies.
4. Tạo `config/db.properties` theo phần cấu hình trên.
5. Chạy `src/main/java/vn/edu/qlcuahang/Main.java`.
6. Trên màn hình chính, bấm **Kiểm tra CSDL**. Khi thấy thông báo kết nối thành công, mở các form quản lý.

## 4. Chạy bằng Maven

Mở terminal tại thư mục dự án:

```bash
mvn clean compile
mvn exec:java
```

Đóng gói thành file JAR có kèm dependencies:

```bash
mvn clean package
```

Sau khi build thành công, file chạy được tạo tại:

`target/QuanLyCuaHangDienThoai.jar`

Chạy JAR từ thư mục gốc dự án để ứng dụng đọc được `config/db.properties`:

```bash
java -jar target/QuanLyCuaHangDienThoai.jar
```

Lưu ý: cần cấu hình SQL Server trước khi mở form dữ liệu. Nếu thiếu cấu hình hoặc kết nối lỗi, ứng dụng sẽ hiện thông báo hướng dẫn kiểm tra.

## 5. Các form và chức năng

### Quản lý sản phẩm
- Hiển thị sản phẩm cùng danh mục.
- Thêm, sửa, xóa, tìm theo tên, hãng, model hoặc danh mục.
- Kiểm tra giá, tồn kho và thời gian bảo hành không âm.
- Không thể xóa sản phẩm đang được hóa đơn/phiếu nhập tham chiếu; SQL Server sẽ bảo vệ quan hệ dữ liệu.

### Quản lý khách hàng
- Thêm, sửa, xóa, tìm theo họ tên, số điện thoại hoặc email.
- Kiểm tra tên, số điện thoại và định dạng email.
- Không thể xóa khách hàng đã được dùng trong hóa đơn.

### Quản lý nhà cung cấp
- Thêm, sửa, xóa, tìm theo tên, số điện thoại hoặc email.
- Số điện thoại và email có thể để trống.
- Không thể xóa nhà cung cấp đang được phiếu nhập tham chiếu.

### Quản lý danh mục
- Thêm, sửa, xóa và tìm danh mục.
- Không thể xóa danh mục còn sản phẩm trực thuộc.

## 6. Đưa dự án lên GitHub

Cách nhanh trên Windows là dùng file `TAO_VA_DAY_LEN_GITHUB.bat`. File này sẽ tạo repository mới bằng tài khoản GitHub của bạn và push mã nguồn lên đó. Cần cài Git for Windows và GitHub CLI (`gh`), sau đó đăng nhập một lần bằng lệnh `gh auth login`. Khi chạy script, chọn **Public** nếu giảng viên cần mở link mà không phải xin quyền. Script yêu cầu xác nhận trước khi tạo repository và push.

Trước khi đưa lên GitHub, hãy kiểm tra `config/db.properties` không nằm trong danh sách file chuẩn bị commit. `.gitignore` đã bỏ qua file này và thư mục `target/`.

Nếu bạn đã tạo repository trống trên trang GitHub, không muốn cài GitHub CLI, hoặc repository đó đã tồn tại, dùng cách Terminal bên dưới. Không chọn tạo sẵn README, `.gitignore` hoặc License vì dự án đã có các file này. Mở Terminal/PowerShell tại thư mục dự án và chạy:

```bash
git init
git add .
git status
git commit -m "Initial Java Swing SQL Server project"
git branch -M main
git remote add origin https://github.com/USERNAME/quan-ly-cua-hang-dien-thoai.git
git push -u origin main
```

Thay `USERNAME` bằng tên tài khoản GitHub của bạn và dùng URL repository vừa tạo. Nếu Git báo thiếu tên/email, cấu hình một lần:

```bash
git config --global user.name "Tên của bạn"
git config --global user.email "email-cua-ban@example.com"
```

Sau khi push thành công, mở trang repository trên GitHub và sao chép URL dạng `https://github.com/USERNAME/quan-ly-cua-hang-dien-thoai` để gửi giảng viên. Không đưa mật khẩu SQL Server, token GitHub hoặc thông tin bí mật vào mã nguồn.

Bạn cũng có thể chạy file `PUSH_TO_GITHUB.bat` ở thư mục gốc; script sẽ hỏi URL repository rồi thực hiện các lệnh Git ở trên. Cần cài Git for Windows và đăng nhập GitHub khi Git yêu cầu xác thực.

## 7. Cấu trúc thư mục

```text
DoAn_Java_QLCuaHangDienThoai/
├── .github/workflows/maven.yml
├── config/
│   └── db.properties.example
├── database/
│   ├── 01_TaoCSDL_QLCuaHangDienThoai.sql
│   └── 02_DuLieuMau.sql
├── docs/
│   ├── BaoCao_ThietKeCSDL_QLCuaHangDienThoai.docx
│   ├── SoDoQuanHe_CSDL.png
│   └── SoDoQuanHe_CSDL.svg
├── src/main/java/vn/edu/qlcuahang/
│   ├── Main.java
│   ├── config/Db.java
│   └── ui/
│       ├── DashboardFrame.java
│       ├── ProductFrame.java
│       ├── CustomerFrame.java
│       ├── SupplierFrame.java
│       ├── CategoryFrame.java
│       └── Ui.java
├── pom.xml
├── PUSH_TO_GITHUB.bat
├── GITHUB_SUBMISSION.md
├── KIEM_THU.md
└── README.md
```

## Kiểm thử và giới hạn hiện tại

Mã nguồn đã được biên dịch bằng `javac --release 17` thành công. Môi trường tạo bộ hồ sơ không có SQL Server được cấu hình cho dự án và không có Maven CLI, nên chưa thể xác nhận kết nối JDBC hoặc chạy `mvn clean package` tại đây. Sau khi push lên GitHub, workflow trong `.github/workflows/maven.yml` sẽ tự động build dự án; bạn vẫn cần chạy thử với SQL Server trên máy cá nhân. Xem `KIEM_THU.md` để biết checklist kiểm tra.

## Phạm vi hiện tại

Đây là phiên bản đồ án tập trung vào các chức năng CRUD và tìm kiếm để đáp ứng yêu cầu tối thiểu. Chức năng lập hóa đơn/chi tiết hóa đơn, đăng nhập và thống kê chưa được triển khai trong giao diện Java; các bảng tương ứng đã có trong cơ sở dữ liệu để có thể mở rộng tiếp.
