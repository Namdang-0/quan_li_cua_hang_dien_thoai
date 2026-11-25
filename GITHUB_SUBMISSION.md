# Hướng dẫn nộp đồ án qua GitHub

## Cách nhanh: tạo repository và push tự động (Windows)

1. Cài [Git for Windows](https://git-scm.com/download/win) và [GitHub CLI](https://cli.github.com/).
2. Mở PowerShell và chạy `gh auth login`; chọn GitHub.com, HTTPS và đăng nhập qua trình duyệt.
3. Nhấp đúp `TAO_VA_DAY_LEN_GITHUB.bat` trong thư mục dự án.
4. Nhập tên repository hoặc nhấn Enter để dùng `quan-ly-cua-hang-dien-thoai`.
5. Chọn Public để giảng viên xem được bằng link (khuyến nghị), xác nhận tạo và push.
6. Script mở trang repository trên GitHub. Sao chép URL trang đó gửi lên hệ thống của trường.

Nếu không muốn dùng GitHub CLI, làm theo cách thủ công bên dưới.

## Trước khi tải lên

- Chạy hai file SQL theo thứ tự trong thư mục `database/`.
- Cấu hình `config/db.properties` trên máy cá nhân và thử nút **Kiểm tra CSDL**.
- Mở từng form, thử thêm/sửa/xóa/tìm kiếm dữ liệu.
- Đảm bảo `config/db.properties` không xuất hiện trong các file chuẩn bị commit. File này được `.gitignore` loại trừ.

## Cách nhanh trên Windows

1. Cài Git for Windows.
2. Tạo repository trống trên GitHub. Chọn Public nếu giảng viên cần xem trực tiếp; không chọn tạo README, `.gitignore` hoặc License.
3. Giải nén dự án, mở thư mục gốc `DoAn_Java_QLCuaHangDienThoai`.
4. Nhấp đúp `PUSH_TO_GITHUB.bat`, dán URL repository kết thúc bằng `.git`, xác nhận `Y`.
5. Hoàn tất xác thực GitHub khi Git yêu cầu.
6. Mở repository, kiểm tra mã nguồn và trạng thái tab Actions. Sao chép URL repository gửi lên hệ thống của trường.

## Cách dùng Terminal

Mở PowerShell tại thư mục gốc dự án và chạy:

```bash
git init
git add .
git status
git commit -m "Submit Java Swing phone store management project"
git branch -M main
git remote add origin https://github.com/USERNAME/quan-ly-cua-hang-dien-thoai.git
git push -u origin main
```

Thay URL bằng URL repository thật. Nếu `origin` đã tồn tại, dùng `git remote set-url origin <URL>`.

## Link nộp

Sau khi push thành công, link gửi giảng viên có dạng:

`https://github.com/USERNAME/quan-ly-cua-hang-dien-thoai`

Không gửi link mẫu này khi chưa thay `USERNAME` và chưa tạo repository thật. Nếu repository Private, hãy mời giảng viên bằng tài khoản GitHub của họ trước khi nộp.
