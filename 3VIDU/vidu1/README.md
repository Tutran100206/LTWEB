# Ví dụ 1 — Đăng nhập bằng email

Spring Boot 4.1.1 / Java 26 / SQL Server / Security 7 / MapStruct 1.6.3. Xem [hướng dẫn cấu hình ở root](../README.md).

1. Tạo database bằng `../sql/create-database.sql`.
2. Sao chép `.env.example` thành `.env`, điền `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
3. Từ root chạy `.\mvnw.cmd -pl vidu1 spring-boot:run`.
4. Mở http://localhost:8081.

| Vai trò | Email | Mật khẩu mẫu |
| --- | --- | --- |
| ADMIN | `admin@vidu1.local` | `Demo@12345` |
| USER | `user@vidu1.local` | `Demo@12345` |

`GET /login` hiển thị form, Spring Security xử lý `POST /login` với `email`/`password`; thành công chuyển `/dashboard`. Header lấy principal và hiển thị họ tên, email, role. Layout chỉ dùng `th:replace` và `fragments/header.html`, `fragments/footer.html`; không có Layout Dialect.

Kiểm tra thủ công: đăng nhập USER, xem header/dashboard, vào `/dashboard/admin` phải nhận 403; đăng xuất bằng nút POST; đăng nhập ADMIN và kiểm tra trang quản trị. Mật khẩu sai bị từ chối. Mỗi POST form giữ CSRF.

Test: `.\mvnw.cmd -pl vidu1 test` từ root. Seed không tạo trùng và không thay mật khẩu đã lưu khi khởi động lại.
