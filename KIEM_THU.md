# Biên bản kiểm tra kỹ thuật

Dự án: Phần mềm quản lý cửa hàng điện thoại — Java Swing + Microsoft SQL Server.

## Kiểm tra đã thực hiện

- Đã biên dịch toàn bộ mã nguồn Java bằng JDK 21 ở chế độ tương thích Java 17 (`javac --release 17 -encoding UTF-8`): **đạt**.
- Đã rà soát sự hiện diện của 4 form dữ liệu: sản phẩm, khách hàng, nhà cung cấp và danh mục.
- Mỗi form dữ liệu có các thao tác thêm, cập nhật, xóa, tìm kiếm và tải lại; câu lệnh SQL dùng `PreparedStatement`.
- Đã rà soát tên bảng/cột mà các form truy vấn so với script SQL trong thư mục `database/`.
- Đã thêm GitHub Actions để tự động chạy `mvn clean package` khi push lên nhánh `main` hoặc tạo pull request.

## Giới hạn kiểm tra

- Môi trường tạo bộ hồ sơ không có máy chủ SQL Server được cấu hình cho dự án, vì vậy chưa thể kiểm thử kết nối JDBC và thao tác CRUD với dữ liệu thật trên máy của người học.
- Maven CLI không có sẵn trong môi trường tạo hồ sơ; do đó chưa thực hiện được `mvn clean package` tại đây. GitHub Actions sẽ thực hiện kiểm tra build sau khi mã nguồn được đẩy lên repository, và người học cần chạy thử trên máy cá nhân sau khi cấu hình SQL Server.

## Trước khi nộp

1. Chạy hai script SQL trên SSMS theo đúng thứ tự trong README.
2. Tạo `config/db.properties` từ file mẫu và điền thông tin SQL Server của máy cá nhân.
3. Chạy ứng dụng, bấm **Kiểm tra CSDL**.
4. Thử thêm, sửa, xóa, tìm kiếm ở cả bốn form.
5. Đọc tab Actions trong GitHub để xác nhận quy trình build đã hoàn tất.
