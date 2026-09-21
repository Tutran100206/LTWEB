# Ví dụ 3 — OTP, User và Product

Xem [hướng dẫn chung](../README.md) để tạo database, JDK 26 và cấu hình `.env`. Module cần DB, SMTP và Cloudinary cho toàn bộ chức năng.

```powershell
# Chạy từ root
.\mvnw.cmd -pl vidu3 spring-boot:run
```

URL: http://localhost:8083.

| Vai trò | Username | Email | Mật khẩu mẫu |
| --- | --- | --- | --- |
| ADMIN | `admin` | `admin@vidu3.local` | `Demo@12345` |
| USER | `user` | `user@vidu3.local` | `Demo@12345` |

Seed tạo các role, hai user đã kích hoạt và một sản phẩm không ảnh; không gọi Cloudinary. Muốn kiểm tra OTP, đăng ký bằng email thật nhận được thư.

## Luồng xác thực

- `/register`: username 3–50 ký tự chữ/số/`_.-`, email hợp lệ, họ tên, mật khẩu tối thiểu 8 ký tự và tối đa 72 byte UTF-8, xác nhận khớp. User mới bị vô hiệu hóa cho đến khi xác thực.
- `/verify-otp`: nhập email và OTP đăng ký; thành công kích hoạt user, tiêu thụ OTP, chuyển đăng nhập.
- `POST /resend-register-otp`: gửi lại, chờ tối thiểu 60 giây giữa các lần yêu cầu.
- `/login`: username hoặc email, BCrypt và session Spring Security. `/logout` là POST, hủy session.
- `/forgot-password`: gửi OTP RESET_PASSWORD cho email đang hoạt động; luôn phản hồi chung để hạn chế tiết lộ tài khoản.
- `/reset-password`: email, OTP, mật khẩu mới và xác nhận. Không chấp nhận OTP đăng ký để reset.

OTP gồm 6 chữ số từ `SecureRandom`, chỉ lưu BCrypt hash; hiệu lực 5 phút, dùng một lần, tối đa 5 lần nhập sai. Gửi lại vô hiệu hóa mã cũ. Transaction khóa bản ghi user để tránh consume/resend đồng thời; số lần sai vẫn được lưu khi trả lỗi OTP. Nếu gửi email thất bại, transaction cấp mã/đăng ký rollback.

## Quản lý người dùng

ADMIN dùng `/users`: tạo, sửa, xóa, tìm theo username/email/họ tên; phân trang, chọn số dòng; hiển thị role, enabled, số sản phẩm, tổng user toàn hệ thống và số kết quả tìm được.

Mật khẩu trống khi sửa nghĩa là giữ nguyên. Không cho tự xóa, tự khóa hoặc tự hạ quyền tài khoản đang dùng. User còn sản phẩm phải xóa/chuyển sản phẩm trước khi xóa user; giao diện báo lỗi rõ ràng. Đổi email qua quản trị hủy OTP gắn email cũ.

## Quản lý sản phẩm

USER chỉ tìm/xem/sửa/xóa sản phẩm của mình, ADMIN quản lý tất cả và chọn chủ sở hữu. Backend kiểm tra quyền theo principal, không tin `userId` do USER gửi. Các thao tác ghi là POST, có CSRF; tìm kiếm/phân trang là GET.

Tên, mô tả, giá, ảnh, owner và ngày tạo được hiển thị. Không chọn ảnh vẫn lưu được; khi sửa giữ ảnh cũ. Ảnh JPG/PNG/GIF tối đa 5 MB, kiểm tra nội dung ảnh trước khi upload.

Thay ảnh: upload mới, lưu URL/publicId, xóa ảnh cũ sau khi DB commit. Rollback DB thì cố gắng xóa ảnh vừa upload. Xóa sản phẩm cũng xóa ảnh sau commit. Nếu Cloudinary từ chối xóa, ứng dụng ghi publicId vào log để quản trị viên xử lý lại; không làm mất dữ liệu DB vì lỗi dọn ảnh bên ngoài.

## Checklist chạy thật

1. Đăng ký email thật, thử login trước verify (phải bị từ chối), thử OTP sai/đúng, dùng lại mã đã consume và resend.
2. Quên mật khẩu, thử OTP hết hạn hoặc sai loại, đặt mật khẩu mới và đăng nhập lại.
3. Đăng nhập ADMIN, tìm kiếm/phân trang User, tạo/sửa/xóa user, kiểm tra validation trùng email/username.
4. Tạo sản phẩm bằng USER với và không có ảnh; thay ảnh, xóa ảnh/sản phẩm trên Cloudinary.
5. Dùng một USER khác truy cập trực tiếp đường dẫn edit/delete sản phẩm không thuộc mình: phải nhận 403. USER vào `/users` cũng nhận 403.
6. ADMIN quản lý sản phẩm của mọi user, chuyển chủ sở hữu; kiểm tra số sản phẩm theo user.
7. Restart: tài khoản không bị tạo trùng hoặc đặt lại mật khẩu.

Test tự động: `.\mvnw.cmd -pl vidu3 test`. Test không gửi email hay ảnh thật và không ghi SQL Server; cần credentials để xác minh các tích hợp thực tế.
