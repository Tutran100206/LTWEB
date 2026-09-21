# Ví dụ 2 — Username hoặc email

Xem [cấu hình chung](../README.md). Sao chép `.env.example` thành `.env` và điền ba biến DB. Từ root chạy:

```powershell
.\mvnw.cmd -pl vidu2 spring-boot:run
```

URL: http://localhost:8082.

| Vai trò | Username | Email | Mật khẩu mẫu |
| --- | --- | --- | --- |
| ADMIN | `admin` | `admin@vidu2.local` | `Demo@12345` |
| USER | `user` | `user@vidu2.local` | `Demo@12345` |

Ô `login` nhận username hoặc email; service chuẩn hóa trim/lowercase rồi tìm bằng repository. Username và email là UNIQUE. Authority dùng `ROLE_USER` / `ROLE_ADMIN`.

`CustomUserDetails` chứa id, username, email, password BCrypt, fullName, images, role, enabled. Header đọc authenticated principal; `images` null/rỗng dùng `/images/avatar.svg`. DTO hiển thị không có password. MapStruct ánh xạ `role.name` sang `roleName`.

Layout Dialect 4.0.1 dùng `layouts/layout.html`, `layout:decorate`, `layout:fragment`, header/footer fragments. CSRF vẫn bật; logout dùng POST.

Kiểm tra thủ công: thử cả `user` và `user@vidu2.local`, kiểm tra fullName/username/email/avatar trên header, thử admin route bằng hai vai trò, rồi logout. Test: `.\mvnw.cmd -pl vidu2 test`.
